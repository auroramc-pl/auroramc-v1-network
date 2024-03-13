package pl.auroramc.lobby;

import static org.bukkit.Bukkit.getOnlinePlayers;

import org.bukkit.entity.Player;
import pl.auroramc.lobby.message.MessageSource;

class VoidTeleportationTask implements Runnable {

  private static final int TELEPORT_SINCE_Y = 20;
  private final LobbyConfig lobbyConfig;
  private final MessageSource messageSource;

  VoidTeleportationTask(final LobbyConfig lobbyConfig, final MessageSource messageSource) {
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
  }

  @Override
  public void run() {
    for (final Player player : getOnlinePlayers()) {
      if (player.getLocation().getY() <= TELEPORT_SINCE_Y) {
        player.teleport(lobbyConfig.spawn);
        player.sendMessage(messageSource.teleportedFromVoid.into());
      }
    }
  }
}
