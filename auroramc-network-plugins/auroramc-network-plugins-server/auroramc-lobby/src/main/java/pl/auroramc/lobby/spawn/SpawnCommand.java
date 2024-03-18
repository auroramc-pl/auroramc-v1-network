package pl.auroramc.lobby.spawn;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.lobby.LobbyConfig;
import pl.auroramc.lobby.message.MutableMessageSource;

@Command(name = "spawn")
public class SpawnCommand {

  private final LobbyConfig lobbyConfig;
  private final MutableMessageSource messageSource;

  public SpawnCommand(final LobbyConfig lobbyConfig, final MutableMessageSource messageSource) {
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
  }

  @Execute
  public CompletableFuture<MutableMessage> spawn(final @Context Player invoker) {
    return invoker.teleportAsync(lobbyConfig.spawn)
        .thenApply(state -> messageSource.teleportedIntoSpawn);
  }
}
