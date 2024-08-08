package pl.auroramc.registry.locale;

import java.util.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

// todo: separate that from multiplatform api
public class LocaleChangedEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final Locale from;
  private final Locale into;

  public LocaleChangedEvent(final Player player, final Locale from, final Locale into) {
    this.player = player;
    this.from = from;
    this.into = into;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  public Player getPlayer() {
    return player;
  }

  public Locale getFrom() {
    return from;
  }

  public Locale getInto() {
    return into;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }
}
