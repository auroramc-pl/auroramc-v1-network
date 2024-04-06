package pl.auroramc.cheque;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.cheque.ChequeConfig.CHEQUE_CONFIG_FILE_NAME;
import static pl.auroramc.cheque.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.cheque.payment.PaymentFacade.getPaymentFacade;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.config.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.cheque.payment.PaymentFacade;
import pl.auroramc.commons.bukkit.integration.litecommands.BukkitCommandsBuilderProcessor;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.user.UserFacade;

public class ChequeBukkitPlugin extends JavaPlugin {

  private static final String AMOUNT_KEY_ID = "cheque_amount";
  private static final String ISSUER_UNIQUE_ID_KEY_ID = "cheque_issuer_unique_id";
  private static final String ISSUER_USERNAME_KEY_ID = "cheque_issuer_username";
  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final ChequeConfig chequeConfig =
        configFactory.produceConfig(
            ChequeConfig.class, CHEQUE_CONFIG_FILE_NAME, new SerdesCommons());

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler();

    final Scheduler scheduler = getBukkitScheduler(this);

    final NamespacedKey amountKey = new NamespacedKey(this, AMOUNT_KEY_ID);
    final NamespacedKey issuerUniqueIdKey = new NamespacedKey(this, ISSUER_UNIQUE_ID_KEY_ID);
    final NamespacedKey issuerUsernameKey = new NamespacedKey(this, ISSUER_USERNAME_KEY_ID);

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = getFundsCurrency(currencyFacade, chequeConfig.fundsCurrencyId);

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);
    final PaymentFacade paymentFacade =
        getPaymentFacade(scheduler, chequeConfig.paymentBuffer, juliet);
    final ChequeFacade chequeFacade =
        new ChequeService(
            messageSource,
            messageCompiler,
            fundsCurrency,
            userFacade,
            economyFacade,
            paymentFacade,
            amountKey,
            issuerUniqueIdKey,
            issuerUsernameKey);

    registerListeners(
        this,
        new ChequeFinalizationListener(scheduler, messageSource, messageCompiler, chequeFacade));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(
                LiteCommandsAnnotations.of(
                    new ChequeCommand(messageSource, chequeFacade, fundsCurrency, economyFacade)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }

  private Currency getFundsCurrency(
      final CurrencyFacade currencyFacade, final long fundsCurrencyId) {
    return Optional.ofNullable(currencyFacade.getCurrencyById(fundsCurrencyId))
        .orElseThrow(
            () ->
                new ChequeInstantiationException(
                    "Could not find currency with id %d, make sure that the id specified in configuration is proper."
                        .formatted(fundsCurrencyId)));
  }
}
