package pl.auroramc.dailyrewards.nametag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.nametag.NametagFacade;

public class NametagListener implements Listener {

  private final NametagFacade nametagFacade;

  public NametagListener(final NametagFacade nametagFacade) {
    this.nametagFacade = nametagFacade;
  }

  @EventHandler
  public void onNametagInjection(final PlayerJoinEvent event) {
    nametagFacade.inject(event.getPlayer());
  }
}
