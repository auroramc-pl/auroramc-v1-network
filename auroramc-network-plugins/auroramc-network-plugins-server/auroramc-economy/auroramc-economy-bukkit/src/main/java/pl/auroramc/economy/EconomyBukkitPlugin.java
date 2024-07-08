package pl.auroramc.economy;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerFacades;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.economy.EconomyConfig.ECONOMY_CONFIG_FILE_NAME;
import static pl.auroramc.economy.account.AccountFacadeFactory.getAccountFacade;
import static pl.auroramc.economy.currency.CurrencyFacadeFactory.getCurrencyFacade;
import static pl.auroramc.economy.economy.EconomyFacadeFactory.getEconomyFacade;
import static pl.auroramc.economy.integration.placeholderapi.PlaceholderApiIntegrationFactory.getPlaceholderApiIntegration;
import static pl.auroramc.economy.leaderboard.LeaderboardFacade.getLeaderboardFacade;
import static pl.auroramc.economy.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.economy.payment.PaymentFacadeFactory.getPaymentFacade;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

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
import pl.auroramc.commons.bukkit.integration.ExternalIntegration;
import pl.auroramc.commons.bukkit.integration.ExternalIntegrator;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.account.AccountFacade;
import pl.auroramc.economy.balance.BalanceCommand;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyCommand;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.economy.integration.commands.currency.CurrencyArgumentResolver;
import pl.auroramc.economy.leaderboard.LeaderboardCommand;
import pl.auroramc.economy.leaderboard.LeaderboardFacade;
import pl.auroramc.economy.message.MessageSource;
import pl.auroramc.economy.payment.PaymentCommand;
import pl.auroramc.economy.payment.PaymentFacade;
import pl.auroramc.economy.transfer.TransferCommand;
import pl.auroramc.integrations.commands.BukkitCommandsBuilderProcessor;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.SerdesCommons;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.user.UserFacade;

public class EconomyBukkitPlugin extends JavaPlugin {

  private Juliet juliet;
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final EconomyConfig economyConfig =
        configFactory.produceConfig(
            EconomyConfig.class, ECONOMY_CONFIG_FILE_NAME, new SerdesCommons());

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final Logger logger = getLogger();

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);
    final CurrencyFacade currencyFacade = getCurrencyFacade(scheduler, juliet);
    final AccountFacade accountFacade = getAccountFacade(scheduler, logger, juliet);
    final PaymentFacade paymentFacade =
        getPaymentFacade(scheduler, logger, juliet, economyConfig.payment);
    final EconomyFacade economyFacade =
        getEconomyFacade(logger, userFacade, accountFacade, paymentFacade);
    registerFacades(this, Set.of(currencyFacade, accountFacade, paymentFacade, economyFacade));

    final ExternalIntegration placeholderApiIntegration =
        getPlaceholderApiIntegration(this, economyFacade, currencyFacade);
    final ExternalIntegrator externalIntegrator =
        new ExternalIntegrator(
            Map.of(placeholderApiIntegration::isSupportedEnvironment, placeholderApiIntegration));
    externalIntegrator.configure(getServer());

    final LeaderboardFacade leaderboardFacade = getLeaderboardFacade(scheduler, juliet);

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .argument(
                Currency.class,
                new CurrencyArgumentResolver<>(
                    currencyFacade, messageSource.validationRequiresExistingCurrency))
            .commands(
                LiteCommandsAnnotations.of(
                    new BalanceCommand(
                        economyFacade,
                        economyConfig.balance,
                        messageSource.balance,
                        messageCompiler,
                        currencyFacade),
                    new PaymentCommand(
                        userFacade, messageSource.payment, messageCompiler, paymentFacade),
                    new TransferCommand(
                        economyFacade,
                        messageSource.transfer,
                        economyConfig.transfer,
                        currencyFacade),
                    new EconomyCommand(economyFacade, messageSource.economy),
                    new LeaderboardCommand(
                        currencyFacade,
                        messageCompiler,
                        messageSource.leaderboard,
                        leaderboardFacade,
                        economyConfig.leaderboard)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    juliet.close();
    commands.unregister();
  }
}
