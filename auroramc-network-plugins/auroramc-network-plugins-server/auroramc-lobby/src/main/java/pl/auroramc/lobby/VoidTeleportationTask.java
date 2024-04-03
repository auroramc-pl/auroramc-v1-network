package pl.auroramc.lobby;

import static org.bukkit.Bukkit.getOnlinePlayers;

import org.bukkit.entity.Player;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.lobby.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;

class VoidTeleportationTask implements Runnable {

  private static final int TELEPORT_SINCE_Y = 20;
  private final LobbyConfig lobbyConfig;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  VoidTeleportationTask(
      final LobbyConfig lobbyConfig,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  @Override
  public void run() {
    final CompiledMessage message = messageCompiler.compile(messageSource.teleportedFromVoid);
    for (final Player player : getOnlinePlayers()) {
      if (player.getLocation().getY() > TELEPORT_SINCE_Y) {
        continue;
      }

      player
          .teleportAsync(lobbyConfig.spawn)
          .thenAccept(state -> message.render(player))
          .exceptionally(CompletableFutureUtils::delegateCaughtException);
    }
  }
}
