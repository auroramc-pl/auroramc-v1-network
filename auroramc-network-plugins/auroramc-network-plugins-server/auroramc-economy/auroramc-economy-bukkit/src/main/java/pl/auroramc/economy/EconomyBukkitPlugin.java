package pl.auroramc.economy;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_NOT_FOUND;
import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.commons.BukkitUtils.registerServices;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.message.MutableMessage.LINE_SEPARATOR;
import static pl.auroramc.economy.EconomyConfig.ECONOMY_CONFIG_FILE_NAME;
import static pl.auroramc.economy.EconomyFacadeFactory.getEconomyFacade;
import static pl.auroramc.economy.account.AccountFacadeFactory.getAccountFacade;
import static pl.auroramc.economy.balance.leaderboard.LeaderboardFacade.getLeaderboardFacade;
import static pl.auroramc.economy.currency.CurrencyFacadeFactory.produceCurrencyFacade;
import static pl.auroramc.economy.integration.placeholderapi.PlaceholderApiIntegrationFactory.producePlaceholderApiIntegration;
import static pl.auroramc.economy.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.economy.message.MutableMessageVariableKey.SCHEMATICS_VARIABLE_KEY;
import static pl.auroramc.economy.payment.PaymentFacadeFactory.producePaymentFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.ExternalIntegration;
import pl.auroramc.commons.integration.ExternalIntegrator;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.account.AccountFacade;
import pl.auroramc.economy.balance.BalanceCommand;
import pl.auroramc.economy.balance.leaderboard.LeaderboardCommand;
import pl.auroramc.economy.balance.leaderboard.LeaderboardFacade;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MutableMessageSource;
import pl.auroramc.economy.payment.PaymentCommand;
import pl.auroramc.economy.payment.PaymentFacade;
import pl.auroramc.economy.rest.server.RestServerExtension;
import pl.auroramc.economy.transfer.TransferCommand;
import pl.auroramc.registry.user.UserFacade;

public class EconomyBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;
  private RestServerExtension restServerExtension;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final EconomyConfig economyConfig = configFactory.produceConfig(
        EconomyConfig.class, ECONOMY_CONFIG_FILE_NAME, new SerdesCommons()
    );

    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final Logger logger = getLogger();

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final CurrencyFacade currencyFacade = produceCurrencyFacade(juliet);
    final AccountFacade accountFacade = getAccountFacade(logger, juliet);
    final PaymentFacade paymentFacade = producePaymentFacade(logger, juliet, economyConfig.payment.paymentBuffer);
    final EconomyFacade economyFacade = getEconomyFacade(logger, userFacade, accountFacade, paymentFacade);
    registerServices(this, Set.of(currencyFacade, accountFacade, paymentFacade, economyFacade));

    final ExternalIntegration placeholderApiIntegration = producePlaceholderApiIntegration(
        this, economyFacade, currencyFacade
    );
    final ExternalIntegrator externalIntegrator = new ExternalIntegrator(
        Map.of(
            placeholderApiIntegration::isSupportedEnvironment,
            placeholderApiIntegration
        )
    );
    externalIntegrator.configure(getServer());

    final LeaderboardFacade leaderboardFacade = getLeaderboardFacade(juliet);

    commands = LiteBukkitFactory.builder(getName(), this)
        .extension(new LiteAdventureExtension<>(),
            configurer -> configurer.miniMessage(true)
        )
        .message(INVALID_USAGE,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_VARIABLE_KEY, context.getSchematic().join(LINE_SEPARATOR))
        )
        .message(MISSING_PERMISSIONS, messageSource.executionOfCommandIsNotPermitted)
        .message(PLAYER_ONLY, messageSource.executionFromConsoleIsUnsupported)
        .message(PLAYER_NOT_FOUND, messageSource.specifiedPlayerIsUnknown)
        .commands(
            LiteCommandsAnnotations.of(
                new BalanceCommand(
                    logger, economyFacade, economyConfig.balance, messageSource, currencyFacade
                ),
                new PaymentCommand(
                    userFacade, messageSource, paymentFacade
                ),
                new TransferCommand(
                    logger, messageSource, economyFacade, economyConfig.transfer, currencyFacade
                ),
                new EconomyCommand(
                    logger, messageSource, economyFacade, currencyFacade
                ),
                new LeaderboardCommand(
                    messageSource, currencyFacade, leaderboardFacade, economyConfig.leaderboard
                )
            )
        )
        .result(MutableMessage.class, new MutableMessageResultHandler<>())
        .build();

    restServerExtension = new RestServerExtension(economyConfig, economyFacade, currencyFacade);
    restServerExtension.enableRestServerIfConfigured();
  }

  @Override
  public void onDisable() {
    commands.unregister();

    restServerExtension.disableRestServerIfRunning();
  }
}
