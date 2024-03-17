package pl.auroramc.auctions.message;

import static org.bukkit.Bukkit.getOnlinePlayers;

import java.util.Collection;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.commons.message.MutableMessage;

public interface MessageFacade {

  static MessageFacade getMessageFacade(final AudienceFacade audienceFacade) {
    return new MessageService(audienceFacade);
  }

  void deliverMessage(
      final Player target, final MutableMessage message
  );

  void deliverMessage(
      final Collection<? extends Player> targets, final MutableMessage message
  );

  default void deliverMessageToOnlinePlayers(final MutableMessage message) {
    deliverMessage(getOnlinePlayers(), message);
  }
}
