package pl.auroramc.gamble;

import static java.lang.String.join;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.gamble.GambleConfig.GAMBLING_CONFIG_FILE_NAME;
import static pl.auroramc.gamble.gamble.GambleFacade.getGambleFacade;
import static pl.auroramc.gamble.stake.StakeFacade.getStakeFacade;
import static pl.auroramc.gamble.stake.view.StakeViewFacade.getStakeViewFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.gamble.coinflip.CoinflipCommand;
import pl.auroramc.gamble.gamble.GambleFacade;
import pl.auroramc.gamble.stake.StakeCommand;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.gamble.stake.view.StakeViewFacade;
import pl.auroramc.gamble.stake.view.StakeViewListener;

public class GambleBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(), YamlBukkitConfigurer::new
    );

    final GambleConfig gambleConfig = configFactory.produceConfig(
        GambleConfig.class, GAMBLING_CONFIG_FILE_NAME
    );

    final Logger logger = getLogger();

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency =
        Optional.ofNullable(currencyFacade.getCurrencyById(gambleConfig.fundsCurrencyId))
            .orElseThrow(() ->
                new GambleInstantiationException(
                    "Could not resolve funds currency, make sure that the currency's id is valid."
                )
            );
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final GambleFacade gambleFacade = getGambleFacade(logger, fundsCurrency, economyFacade);

    final StakeFacade stakeFacade = getStakeFacade();
    final StakeViewFacade stakeViewFacade = getStakeViewFacade(stakeFacade, fundsCurrency);

    registerListeners(this,
        new StakeViewListener(
            this, logger, fundsCurrency, economyFacade, gambleFacade, stakeFacade, stakeViewFacade
        )
    );

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(
                miniMessage().deserialize(
                    "<red>Nie możesz użyć tej konsoli z poziomu konsoli."
                )
            )
        )
        .commandInstance(
            new CoinflipCommand(
                logger, stakeFacade, stakeViewFacade, fundsCurrency, economyFacade
            )
        )
        .commandInstance(new StakeCommand(stakeViewFacade))
        .argument(Player.class,
            new BukkitPlayerArgument<>(getServer(),
                miniMessage().deserialize(
                    "<red>Gracz o wskazanej przez ciebie nazwie jest Offline."
                )
            )
        )
        .redirectResult(RequiredPermissions.class, Component.class,
            context -> miniMessage().deserialize(
                "<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."
            )
        )
        .redirectResult(Schematic.class, Component.class,
            context -> miniMessage().deserialize(
                "<red>Poprawne użycie: <yellow><newline><schematics>",
                parsed("schematics", join("<newline>", context.getSchematics()))
            )
        )
        .register();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();
  }
}
