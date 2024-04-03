package pl.auroramc.lobby.spawn;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.lobby.LobbyConfig;
import pl.auroramc.lobby.message.MessageSource;
import pl.auroramc.messages.message.MutableMessage;

@Command(name = "spawn")
public class SpawnCommand {

  private final LobbyConfig lobbyConfig;
  private final MessageSource messageSource;

  public SpawnCommand(final LobbyConfig lobbyConfig, final MessageSource messageSource) {
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
  }

  @Execute
  public CompletableFuture<MutableMessage> spawn(final @Context Player invoker) {
    return invoker
        .teleportAsync(lobbyConfig.spawn)
        .thenApply(state -> messageSource.teleportedIntoSpawn)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
