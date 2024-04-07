package pl.auroramc.lobby;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.lobby.LobbyConfig.LOBBY_CONFIG_FILE_NAME;
import static pl.auroramc.lobby.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.bukkit.integration.litecommands.BukkitCommandsBuilderProcessor;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.lobby.message.MessageSource;
import pl.auroramc.lobby.spawn.SpawnCommand;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class LobbyBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final LobbyConfig lobbyConfig =
        configFactory.produceConfig(LobbyConfig.class, LOBBY_CONFIG_FILE_NAME, new SerdesBukkit());

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    scheduler.schedule(
        SYNC, new VoidTeleportationTask(lobbyConfig, messageSource, messageCompiler), ofSeconds(1));

    registerListeners(this, new LobbyListener(lobbyConfig, messageSource, messageCompiler));

    commands =
        LiteBukkitFactory.builder(getName(), this)
            .extension(new LiteAdventureExtension<>(), configurer -> configurer.miniMessage(true))
            .commands(LiteCommandsAnnotations.of(new SpawnCommand(lobbyConfig, messageSource)))
            .selfProcessor(
                new BukkitCommandsBuilderProcessor(messageSource.command, messageCompiler))
            .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }
}
