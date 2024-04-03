package pl.auroramc.cheque;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_NOT_FOUND;
import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.cheque.ChequeConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.cheque.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.cheque.message.MutableMessageVariableKey.SCHEMATICS_PATH;
import static pl.auroramc.cheque.payment.PaymentFacade.getPaymentFacade;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.message.MutableMessage.LINE_SEPARATOR;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.cheque.message.MutableMessageSource;
import pl.auroramc.cheque.payment.PaymentFacade;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.registry.user.UserFacade;

public class ChequeBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final ChequeConfig chequeConfig =
        configFactory.produceConfig(ChequeConfig.class, PLUGIN_CONFIG_FILE_NAME);

    final Logger logger = getLogger();

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    final Juliet juliet =
        JulietBuilder.newBuilder()
            .withDataSource(produceHikariDataSource(julietConfig.hikari))
            .build();

    final MutableMessageSource messageSource =
        configFactory.produceConfig(
            MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource());

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency =
        Optional.ofNullable(currencyFacade.getCurrencyById(chequeConfig.fundsCurrencyId))
            .orElseThrow(
                () ->
                    new ChequeInstantiationException(
                        "Could not find currency with id %d, make sure that the id specified in configuration is proper."
                            .formatted(chequeConfig.fundsCurrencyId)));

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);
    final PaymentFacade paymentFacade = getPaymentFacade(logger, juliet);
    final ChequeFacade chequeFacade =
        new ChequeService(
            this, logger, messageSource, fundsCurrency, userFacade, economyFacade, paymentFacade);

    registerListeners(this, new ChequeFinalizationListener(this, logger, chequeFacade));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .message(
                INVALID_USAGE,
                context ->
                    messageSource.availableSchematicsSuggestion.with(
                        SCHEMATICS_PATH, context.getSchematic().join(LINE_SEPARATOR)))
            .message(MISSING_PERMISSIONS, messageSource.executionOfCommandIsNotPermitted)
            .message(PLAYER_ONLY, messageSource.executionFromConsoleIsUnsupported)
            .message(PLAYER_NOT_FOUND, messageSource.specifiedPlayerIsUnknown)
            .commands(
                LiteCommandsAnnotations.of(
                    new ChequeCommand(
                        logger, messageSource, chequeFacade, fundsCurrency, economyFacade)))
            .result(MutableMessage.class, new MutableMessageResultHandler<>())
            .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }
}
