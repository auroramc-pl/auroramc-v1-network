package pl.auroramc.commons.event.publisher;

import static pl.auroramc.commons.BukkitUtils.postToMainThread;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class BukkitEventPublisher {

  private final Plugin plugin;

  public BukkitEventPublisher(final Plugin plugin) {
    this.plugin = plugin;
  }

  public void publish(final Event event) {
    postToMainThread(plugin, () -> plugin.getServer().getPluginManager().callEvent(event));
  }
}
