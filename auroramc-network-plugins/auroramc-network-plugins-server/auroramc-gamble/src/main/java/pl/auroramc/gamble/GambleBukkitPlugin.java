package pl.auroramc.gamble;

import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.gamble.GambleConfig.GAMBLING_CONFIG_FILE_NAME;
import static pl.auroramc.gamble.gamble.GambleFacade.getGambleFacade;
import static pl.auroramc.gamble.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.gamble.stake.StakeFacade.getStakeFacade;
import static pl.auroramc.gamble.stake.view.StakeViewFacade.getStakeViewFacade;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.bukkit.integration.litecommands.BukkitCommandsBuilderProcessor;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.gamble.coinflip.CoinflipCommand;
import pl.auroramc.gamble.gamble.GambleFacade;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.message.placeholder.transformer.pack.GambleObjectTransformerPack;
import pl.auroramc.gamble.stake.StakeCommand;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.gamble.stake.view.StakeViewFacade;
import pl.auroramc.gamble.stake.view.StakeViewListener;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class GambleBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final GambleConfig gambleConfig =
        configFactory.produceConfig(GambleConfig.class, GAMBLING_CONFIG_FILE_NAME);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler =
        getBukkitMessageCompiler(new GambleObjectTransformerPack());

    final Scheduler scheduler = getBukkitScheduler(this);

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = getFundsCurrency(currencyFacade, gambleConfig.fundsCurrencyId);
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final GambleFacade gambleFacade =
        getGambleFacade(fundsCurrency, messageSource, messageCompiler, economyFacade);

    final StakeFacade stakeFacade = getStakeFacade();
    final StakeViewFacade stakeViewFacade =
        getStakeViewFacade(scheduler, stakeFacade, fundsCurrency, messageSource, messageCompiler);

    registerListeners(
        this,
        new StakeViewListener(
            scheduler,
            fundsCurrency,
            messageSource,
            messageCompiler,
            economyFacade,
            gambleFacade,
            stakeFacade,
            stakeViewFacade));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(
                LiteCommandsAnnotations.of(
                    new CoinflipCommand(
                        stakeFacade, stakeViewFacade, fundsCurrency, messageSource, economyFacade),
                    new StakeCommand(messageSource, stakeViewFacade)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }

  private Currency getFundsCurrency(
      final CurrencyFacade currencyFacade, final long fundsCurrencyId) {
    return Optional.ofNullable(currencyFacade.getCurrencyById(fundsCurrencyId))
        .orElseThrow(
            () ->
                new GambleInstantiationException(
                    "Could not resolve funds currency, make sure that the currency's id is valid."));
  }
}
