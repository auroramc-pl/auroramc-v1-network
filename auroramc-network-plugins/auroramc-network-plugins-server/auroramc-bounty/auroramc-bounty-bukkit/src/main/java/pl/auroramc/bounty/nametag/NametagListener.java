package pl.auroramc.bounty.nametag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.auroramc.nametag.NametagFacade;
import pl.auroramc.nametag.context.NametagContextFacade;

public class NametagListener implements Listener {

  private final NametagFacade nametagFacade;
  private final NametagContextFacade nametagContextFacade;

  public NametagListener(
      final NametagFacade nametagFacade, final NametagContextFacade nametagContextFacade) {
    this.nametagFacade = nametagFacade;
    this.nametagContextFacade = nametagContextFacade;
  }

  @EventHandler
  public void onNametagInject(final PlayerJoinEvent event) {
    nametagFacade.inject(event.getPlayer());
  }

  @EventHandler
  public void onNametagDetach(final PlayerQuitEvent event) {
    nametagContextFacade.deleteNametagContext(event.getPlayer().getUniqueId());
  }
}
