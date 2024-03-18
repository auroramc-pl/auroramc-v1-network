package pl.auroramc.lobby;

import static org.bukkit.Bukkit.getOnlinePlayers;

import org.bukkit.entity.Player;
import pl.auroramc.lobby.message.MutableMessageSource;

class VoidTeleportationTask implements Runnable {

  private static final int TELEPORT_SINCE_Y = 20;
  private final LobbyConfig lobbyConfig;
  private final MutableMessageSource messageSource;

  VoidTeleportationTask(final LobbyConfig lobbyConfig, final MutableMessageSource messageSource) {
    this.lobbyConfig = lobbyConfig;
    this.messageSource = messageSource;
  }

  @Override
  public void run() {
    for (final Player player : getOnlinePlayers()) {
      if (player.getLocation().getY() <= TELEPORT_SINCE_Y) {
        player.teleport(lobbyConfig.spawn);
        player.sendMessage(messageSource.teleportedFromVoid.compile());
      }
    }
  }
}
