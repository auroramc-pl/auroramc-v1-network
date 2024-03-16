package pl.auroramc.economy;

import static java.lang.String.join;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.commons.BukkitUtils.registerServices;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.economy.EconomyConfig.ECONOMY_CONFIG_FILE_NAME;
import static pl.auroramc.economy.EconomyFacadeFactory.getEconomyFacade;
import static pl.auroramc.economy.account.AccountFacadeFactory.getAccountFacade;
import static pl.auroramc.economy.balance.leaderboard.LeaderboardFacade.getLeaderboardFacade;
import static pl.auroramc.economy.currency.CurrencyFacadeFactory.produceCurrencyFacade;
import static pl.auroramc.economy.integration.placeholderapi.PlaceholderApiIntegrationFactory.producePlaceholderApiIntegration;
import static pl.auroramc.economy.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.economy.message.MessageVariableKey.SCHEMATICS_VARIABLE_KEY;
import static pl.auroramc.economy.payment.PaymentFacadeFactory.producePaymentFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.v2.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.account.AccountFacade;
import pl.auroramc.economy.balance.BalanceCommand;
import pl.auroramc.economy.balance.leaderboard.LeaderboardCommand;
import pl.auroramc.economy.balance.leaderboard.LeaderboardFacade;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.commons.integration.ExternalIntegration;
import pl.auroramc.commons.integration.ExternalIntegrator;
import pl.auroramc.economy.message.MessageSource;
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

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
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

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(
                messageSource.executionFromConsoleIsUnsupported
            )
        )
        .commandInstance(
            new BalanceCommand(
                logger, economyFacade, economyConfig.balance, messageSource, currencyFacade
            )
        )
        .commandInstance(
            new PaymentCommand(userFacade, messageSource, paymentFacade)
        )
        .commandInstance(
            new TransferCommand(
                logger, messageSource, economyFacade, economyConfig.transfer, currencyFacade
            )
        )
        .commandInstance(
            new EconomyCommand(logger, messageSource, economyFacade, currencyFacade)
        )
        .commandInstance(
            new LeaderboardCommand(
                messageSource, currencyFacade, leaderboardFacade, economyConfig.leaderboard
            )
        )
        .argument(Player.class,
            new BukkitPlayerArgument<>(
                getServer(), messageSource.specifiedPlayerIsUnknown
            )
        )
        .redirectResult(RequiredPermissions.class, MutableMessage.class,
            context -> messageSource.executionOfCommandIsNotPermitted
        )
        .redirectResult(Schematic.class, MutableMessage.class,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_VARIABLE_KEY, join("<newline>", context.getSchematics()))
        )
        .resultHandler(MutableMessage.class, new MutableMessageResultHandler())
        .register();

    restServerExtension = new RestServerExtension(economyConfig, economyFacade, currencyFacade);
    restServerExtension.enableRestServerIfConfigured();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();

    restServerExtension.disableRestServerIfRunning();
  }
}
