package pl.auroramc.auctions.message;

import static org.bukkit.Bukkit.getOnlinePlayers;

import java.util.Collection;
import java.util.List;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.commons.Tuple;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;

public interface MessageFacade {

  static MessageFacade getMessageFacade(final AudienceFacade audienceFacade) {
    return new MessageService(audienceFacade);
  }

  void deliverMessage(final DeliverableMutableMessage message);
}
