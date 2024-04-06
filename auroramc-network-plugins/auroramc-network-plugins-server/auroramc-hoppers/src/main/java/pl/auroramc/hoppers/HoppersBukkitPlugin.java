package pl.auroramc.hoppers;

import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.hoppers.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.bukkit.integration.litecommands.BukkitCommandsBuilderProcessor;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.hoppers.hopper.HopperCommand;
import pl.auroramc.hoppers.hopper.HopperInitializeListener;
import pl.auroramc.hoppers.hopper.HopperTransferListener;
import pl.auroramc.hoppers.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class HoppersBukkitPlugin extends JavaPlugin {

  static final String TRANSFER_QUANTITY_KEY_ID = "transfer_quantity";
  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler();

    final Scheduler scheduler = getBukkitScheduler(this);

    final NamespacedKey transferQuantityKey = new NamespacedKey(this, TRANSFER_QUANTITY_KEY_ID);
    CustomBlockData.registerListener(this);

    registerListeners(
        this,
        new HopperInitializeListener(this, transferQuantityKey),
        new HopperTransferListener(this, scheduler, transferQuantityKey));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(
                LiteCommandsAnnotations.of(
                    new HopperCommand(messageSource, messageCompiler, transferQuantityKey)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }
}
