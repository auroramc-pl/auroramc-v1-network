package pl.auroramc.economy;

import static java.lang.String.join;
import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed;
import static pl.auroramc.commons.BukkitUtils.registerServices;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.economy.EconomyBukkitPluginConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.economy.EconomyFacadeFactory.getEconomyFacade;
import static pl.auroramc.economy.account.AccountFacadeFactory.getAccountFacade;
import static pl.auroramc.economy.balance.leaderboad.LeaderboardFacade.getLeaderboardFacade;
import static pl.auroramc.economy.currency.CurrencyFacadeFactory.produceCurrencyFacade;
import static pl.auroramc.economy.integration.placeholderapi.PlaceholderApiIntegrationFactory.producePlaceholderApiIntegration;
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
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.economy.account.AccountFacade;
import pl.auroramc.economy.balance.BalanceCommand;
import pl.auroramc.economy.balance.leaderboad.LeaderboardCommand;
import pl.auroramc.economy.balance.leaderboad.LeaderboardFacade;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.commons.integration.ExternalIntegration;
import pl.auroramc.commons.integration.ExternalIntegrator;
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

    final EconomyBukkitPluginConfig pluginConfig = configFactory.produceConfig(
        EconomyBukkitPluginConfig.class, PLUGIN_CONFIG_FILE_NAME, new SerdesCommons());

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
    final PaymentFacade paymentFacade = producePaymentFacade(logger, juliet, pluginConfig.payment.paymentBuffer);
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
        .contextualBind(Player.class, new BukkitOnlyPlayerContextual<>(
            miniMessage().deserialize("<red>Nie możesz użyć tej komendy z poziomu konsoli!")))
        .commandInstance(
            new BalanceCommand(
                logger, economyFacade, pluginConfig.balance, currencyFacade))
        .commandInstance(
            new PaymentCommand(userFacade, paymentFacade))
        .commandInstance(
            new TransferCommand(
                logger, economyFacade, pluginConfig.transfer, currencyFacade))
        .commandInstance(
            new EconomyCommand(
                logger, economyFacade, currencyFacade))
        .commandInstance(
            new LeaderboardCommand(
                currencyFacade, leaderboardFacade, pluginConfig.leaderboard))
        .argument(Player.class, new BukkitPlayerArgument<>(getServer(),
            miniMessage().deserialize("<red>Gracz o wskazanej przez ciebie nazwie jest Offline.")))
        .redirectResult(RequiredPermissions.class, Component.class, permissions ->
            miniMessage().deserialize("<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."))
        .redirectResult(Schematic.class, Component.class, schematic ->
            miniMessage().deserialize("<red>Poprawne użycie: <yellow><newline><schematics>",
                parsed("schematics", join("<newline>", schematic.getSchematics()))))
        .register();

    restServerExtension = new RestServerExtension(pluginConfig, economyFacade, currencyFacade);
    restServerExtension.enableRestServerIfConfigured();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();
    restServerExtension.disableRestServerIfRunning();
  }
}
