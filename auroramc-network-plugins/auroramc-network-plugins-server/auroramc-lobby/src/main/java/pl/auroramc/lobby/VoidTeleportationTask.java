package pl.auroramc.lobby;

import static org.bukkit.Bukkit.getOnlinePlayers;

import org.bukkit.entity.Player;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.lobby.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.Viewer;

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
    for (final Player player : getOnlinePlayers()) {
      if (player.getLocation().getY() > TELEPORT_SINCE_Y) {
        continue;
      }

      final Viewer viewer = BukkitViewer.wrap(player);
      player
          .teleportAsync(lobbyConfig.spawn)
          .thenAccept(
              state -> viewer.deliver(messageCompiler.compile(messageSource.teleportedFromVoid)))
          .exceptionally(CompletableFutureUtils::delegateCaughtException);
    }
  }
}
