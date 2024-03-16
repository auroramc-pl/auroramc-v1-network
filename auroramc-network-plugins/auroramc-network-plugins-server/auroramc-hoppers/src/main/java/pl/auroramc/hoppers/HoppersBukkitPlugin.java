package pl.auroramc.hoppers;

import static com.jeff_media.customblockdata.CustomBlockData.registerListener;
import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_NOT_FOUND;
import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.hoppers.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.hoppers.message.MessageVariableKey.SCHEMATICS_VARIABLE_KEY;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.hoppers.hopper.HopperCommand;
import pl.auroramc.hoppers.hopper.HopperInitializeListener;
import pl.auroramc.hoppers.hopper.HopperTransferListener;
import pl.auroramc.hoppers.message.MessageSource;

public class HoppersBukkitPlugin extends JavaPlugin {

  static final String TRANSFER_QUANTITY_KEY_ID = "transfer_quantity";
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(
        getDataFolder().toPath(),
        YamlBukkitConfigurer::new
    );
    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final NamespacedKey transferQuantityKey = new NamespacedKey(this, TRANSFER_QUANTITY_KEY_ID);

    registerListener(this);
    registerListeners(this,
        new HopperInitializeListener(this, transferQuantityKey),
        new HopperTransferListener(this, transferQuantityKey)
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
                new HopperCommand(messageSource, transferQuantityKey)
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
