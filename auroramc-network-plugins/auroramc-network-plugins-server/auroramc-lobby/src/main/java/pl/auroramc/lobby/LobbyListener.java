package pl.auroramc.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.lobby.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.Viewer;

class LobbyListener implements Listener {

  private final LobbyConfig lobbyConfig;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  LobbyListener(
      final LobbyConfig lobbyConfig,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  @EventHandler
  public void onLobbyJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    final Viewer viewer = BukkitViewer.wrap(player);
    player
        .teleportAsync(lobbyConfig.spawn)
        .thenApply(state -> messageCompiler.compile(messageSource.lobbyClarification))
        .thenAccept(viewer::deliver)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
