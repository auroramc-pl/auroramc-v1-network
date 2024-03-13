package pl.auroramc.scoreboard.sidebar;

import static org.bukkit.Bukkit.getOnlinePlayers;

import org.bukkit.entity.Player;

public class SidebarRenderingTask implements Runnable {

  private final SidebarRenderer sidebarRenderer;

  public SidebarRenderingTask(final SidebarRenderer sidebarRenderer) {
    this.sidebarRenderer = sidebarRenderer;
  }

  @Override
  public void run() {
    for (final Player viewer : getOnlinePlayers()) {
      sidebarRenderer.render(viewer);
    }
  }
}
