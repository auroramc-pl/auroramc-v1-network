package pl.auroramc.scoreboard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.auroramc.scoreboard.sidebar.SidebarFacade;
import pl.auroramc.scoreboard.sidebar.SidebarRenderer;

class ScoreboardListener implements Listener {

  private final SidebarFacade sidebarFacade;
  private final SidebarRenderer sidebarRenderer;

  ScoreboardListener(final SidebarFacade sidebarFacade, final SidebarRenderer sidebarRenderer) {
    this.sidebarFacade = sidebarFacade;
    this.sidebarRenderer = sidebarRenderer;
  }

  @EventHandler
  public void onSidebarCreationRequest(final PlayerJoinEvent event) {
    sidebarRenderer.render(event.getPlayer());
  }

  @EventHandler
  public void onSidebarDeletionRequest(final PlayerQuitEvent event) {
    sidebarFacade.deleteSidebarByUniqueId(event.getPlayer().getUniqueId());
  }
}
