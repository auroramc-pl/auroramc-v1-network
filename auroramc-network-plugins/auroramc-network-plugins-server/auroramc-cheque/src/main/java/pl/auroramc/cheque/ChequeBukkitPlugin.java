package pl.auroramc.cheque;

import static java.lang.String.join;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.cheque.ChequeConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.cheque.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.cheque.payment.PaymentFacade.getPaymentFacade;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.cheque.payment.PaymentFacade;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.v2.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.registry.user.UserFacade;

public class ChequeBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final ChequeConfig chequeConfig = configFactory.produceConfig(
        ChequeConfig.class, PLUGIN_CONFIG_FILE_NAME
    );

    final Logger logger = getLogger();

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = Optional.ofNullable(currencyFacade.getCurrencyById(chequeConfig.fundsCurrencyId))
        .orElseThrow(() ->
            new ChequeInstantiationException(
                "Could not find currency with id %d, make sure that the id specified in configuration is proper."
                    .formatted(
                        chequeConfig.fundsCurrencyId
                    )
            )
        );

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);
    final PaymentFacade paymentFacade = getPaymentFacade(logger, juliet);
    final ChequeFacade chequeFacade = new ChequeService(
        this, logger, messageSource, fundsCurrency, userFacade, economyFacade, paymentFacade
    );

    registerListeners(this,
        new ChequeFinalizationListener(this, logger, chequeFacade)
    );

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(messageSource.executionFromConsoleIsUnsupported)
        )
        .commandInstance(
            new ChequeCommand(logger, messageSource, chequeFacade, fundsCurrency, economyFacade)
        )
        .argument(Player.class,
            new BukkitPlayerArgument<>(getServer(), messageSource.specifiedPlayerIsOffline)
        )
        .redirectResult(RequiredPermissions.class, MutableMessage.class,
            context -> messageSource.executionOfCommandIsNotPermitted
        )
        .redirectResult(Schematic.class, MutableMessage.class,
            context -> messageSource.availableSchematicsSuggestion
                .with("schematics", join("<newline>", context.getSchematics()))
        )
        .resultHandler(MutableMessage.class, new MutableMessageResultHandler())
        .register();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();
  }
}
