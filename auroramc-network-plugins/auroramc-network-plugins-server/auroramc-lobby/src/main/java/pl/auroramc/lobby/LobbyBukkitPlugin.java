package pl.auroramc.lobby;

import static dev.rollczi.litecommands.bukkit.LiteBukkitMessages.PLAYER_ONLY;
import static dev.rollczi.litecommands.message.LiteMessages.INVALID_USAGE;
import static dev.rollczi.litecommands.message.LiteMessages.MISSING_PERMISSIONS;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.BukkitUtils.getTicksOf;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.lobby.LobbyConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.lobby.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.lobby.message.MutableMessageVariableKey.SCHEMATICS_VARIABLE_KEY;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.adventure.LiteAdventureExtension;
import dev.rollczi.litecommands.annotations.LiteCommandsAnnotations;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.integration.litecommands.MutableMessageResultHandler;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.lobby.message.MutableMessageSource;
import pl.auroramc.lobby.spawn.SpawnCommand;

public class LobbyBukkitPlugin extends JavaPlugin {

  private LiteCommands<CommandSender> commands;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );
    final LobbyConfig lobbyConfig = configFactory.produceConfig(
        LobbyConfig.class, PLUGIN_CONFIG_FILE_NAME, new SerdesBukkit()
    );

    registerListeners(this, new LobbyListener(lobbyConfig, messageSource));

    getServer().getScheduler().runTaskTimer(this,
        new VoidTeleportationTask(lobbyConfig, messageSource),
        getTicksOf(ZERO),
        getTicksOf(ofSeconds(1))
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
        .commands(LiteCommandsAnnotations.of(new SpawnCommand(lobbyConfig, messageSource)))
        .result(MutableMessage.class, new MutableMessageResultHandler<>())
        .build();
  }

  @Override
  public void onDisable() {
    commands.unregister();
  }
}
