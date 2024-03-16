package pl.auroramc.gamble;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_NOT_FOUND;
import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;
import static pl.auroramc.gamble.GambleConfig.GAMBLING_CONFIG_FILE_NAME;
import static pl.auroramc.gamble.gamble.GambleFacade.getGambleFacade;
import static pl.auroramc.gamble.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.gamble.message.MessageVariableKey.SCHEMATICS_VARIABLE_KEY;
import static pl.auroramc.gamble.stake.StakeFacade.getStakeFacade;
import static pl.auroramc.gamble.stake.view.StakeViewFacade.getStakeViewFacade;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.gamble.coinflip.CoinflipCommand;
import pl.auroramc.gamble.gamble.GambleFacade;
import pl.auroramc.gamble.message.MessageSource;
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

    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
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

    final GambleFacade gambleFacade = getGambleFacade(
        logger, fundsCurrency, messageSource, economyFacade
    );

    final StakeFacade stakeFacade = getStakeFacade();
    final StakeViewFacade stakeViewFacade = getStakeViewFacade(
        stakeFacade, fundsCurrency, messageSource
    );

    registerListeners(this,
        new StakeViewListener(
            this,
            logger,
            fundsCurrency,
            messageSource,
            economyFacade,
            gambleFacade,
            stakeFacade,
            stakeViewFacade
        )
    );

    commands = LiteBukkitFactory.builder(getName(), this)
        .extension(new LiteAdventureExtension<>(),
            configurer -> configurer.miniMessage(true)
        )
        .message(INVALID_USAGE,
            context -> messageSource.availableSchematicsSuggestion
                .with(SCHEMATICS_VARIABLE_KEY, context.getSchematic().join("<newline>"))
        )
        .message(MISSING_PERMISSIONS, messageSource.executionOfCommandIsNotPermitted)
        .message(PLAYER_ONLY, messageSource.executionFromConsoleIsUnsupported)
        .message(PLAYER_NOT_FOUND, messageSource.specifiedPlayerIsUnknown)
        .commands(
            LiteCommandsAnnotations.of(
                new CoinflipCommand(
                    logger, stakeFacade, stakeViewFacade, fundsCurrency, messageSource, economyFacade
                ),
                new StakeCommand(
                    messageSource, stakeViewFacade
                )
            )
        )
        .result(MutableMessage.class, new MutableMessageResultHandler<>())
        .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }
}
