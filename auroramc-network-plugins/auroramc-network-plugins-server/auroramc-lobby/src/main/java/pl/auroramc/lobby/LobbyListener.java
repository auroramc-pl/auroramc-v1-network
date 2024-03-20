package pl.auroramc.lobby;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.lobby.message.MutableMessageSource;

class LobbyListener implements Listener {

  private final Logger logger;
  private final LobbyConfig lobbyConfig;
  private final MutableMessageSource messageSource;

  LobbyListener(
      final Logger logger,
      final LobbyConfig lobbyConfig,
      final MutableMessageSource messageSource) {
    this.logger = logger;
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
  }

  @EventHandler
  public void onLobbyJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    player
        .teleportAsync(lobbyConfig.spawn)
        .thenAccept(state -> messageSource.lobbyClarification.deliver(player))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
