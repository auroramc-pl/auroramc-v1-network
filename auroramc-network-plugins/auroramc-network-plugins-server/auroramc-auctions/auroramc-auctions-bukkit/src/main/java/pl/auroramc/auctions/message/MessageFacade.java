package pl.auroramc.auctions.message;

import static org.bukkit.Bukkit.getOnlinePlayers;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;

public interface MessageFacade {

  static MessageFacade getMessageFacade(final MessageViewerFacade messageViewerFacade) {
    return new MessageService(messageViewerFacade);
  }

  void deliverMessage(final Player target, final Component message);

  void deliverMessage(final Collection<? extends Player> targets, final Component message);

  default void deliverMessageToOnlinePlayers(final Component message) {
    deliverMessage(getOnlinePlayers(), message);
  }
}
