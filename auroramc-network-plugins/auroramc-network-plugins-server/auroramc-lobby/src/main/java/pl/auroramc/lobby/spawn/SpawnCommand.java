package pl.auroramc.lobby.spawn;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;
import pl.auroramc.lobby.LobbyConfig;
import pl.auroramc.lobby.message.MutableMessageSource;

@Command(name = "spawn")
public class SpawnCommand {

  private final Logger logger;
  private final LobbyConfig lobbyConfig;
  private final MutableMessageSource messageSource;

  public SpawnCommand(
      final Logger logger,
      final LobbyConfig lobbyConfig,
      final MutableMessageSource messageSource) {
    this.logger = logger;
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
  }

  @Execute
  public CompletableFuture<DeliverableMutableMessage> spawn(final @Context Player invoker) {
    return invoker
        .teleportAsync(lobbyConfig.spawn)
        .thenApply(state -> messageSource.teleportedIntoSpawn)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
