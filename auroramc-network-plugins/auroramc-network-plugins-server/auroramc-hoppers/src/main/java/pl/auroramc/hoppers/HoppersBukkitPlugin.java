package pl.auroramc.hoppers;

import static com.jeff_media.customblockdata.CustomBlockData.registerListener;
import static java.lang.String.join;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.hoppers.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.adventure.paper.LitePaperAdventureFactory;
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual;
import dev.rollczi.litecommands.bukkit.tools.BukkitPlayerArgument;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.schematic.Schematic;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.hoppers.hopper.HopperCommand;
import pl.auroramc.hoppers.hopper.HopperInitializeListener;
import pl.auroramc.hoppers.hopper.HopperTransferListener;
import pl.auroramc.hoppers.message.MessageResultHandler;
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

    commands = LitePaperAdventureFactory.builder(getServer(), getName())
        .contextualBind(Player.class,
            new BukkitOnlyPlayerContextual<>(
                messageSource.executionFromConsoleIsUnsupported
            )
        )
        .commandInstance(new HopperCommand(messageSource, transferQuantityKey))
        .argument(Player.class,
            new BukkitPlayerArgument<>(
                getServer(),
                messageSource.specifiedPlayerIsUnknown
            )
        )
        .redirectResult(RequiredPermissions.class, MutableMessage.class,
            context -> messageSource.executionOfCommandIsNotPermitted
        )
        .redirectResult(Schematic.class, MutableMessage.class,
            context -> messageSource.availableSchematicsSuggestion
                .with("schematics", join(", ", context.getSchematics()))
        )
        .resultHandler(MutableMessage.class, new MessageResultHandler())
        .register();
  }

  @Override
  public void onDisable() {
    commands.getPlatform().unregisterAll();
  }
}
