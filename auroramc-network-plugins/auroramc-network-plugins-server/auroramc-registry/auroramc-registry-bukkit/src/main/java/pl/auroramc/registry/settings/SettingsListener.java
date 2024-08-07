package pl.auroramc.registry.settings;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.messages.viewer.BukkitViewerFacade;

public class SettingsListener implements Listener {

  private final BukkitViewerFacade viewerFacade;
  private final SettingsController settingsController;

  public SettingsListener(
      final BukkitViewerFacade viewerFacade, final SettingsController settingsController) {
    this.viewerFacade = viewerFacade;
    this.settingsController = settingsController;
  }

  @EventHandler
  public void onSettingsLoad(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    viewerFacade.createViewerByUniqueId(player.getUniqueId());

    settingsController
        .getOrCreateSettings(player)
        .thenAccept(settings -> settingsController.applySettings(player, settings))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @EventHandler
  public void onSettingsUnload(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();
    viewerFacade.deleteViewerByUniqueId(player.getUniqueId());
  }
}
