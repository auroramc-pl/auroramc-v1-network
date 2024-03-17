package pl.auroramc.auctions.message;

import java.util.Collection;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.commons.message.MutableMessage;

class MessageService implements MessageFacade {

  private final AudienceFacade audienceFacade;

  MessageService(final AudienceFacade audienceFacade) {
    this.audienceFacade = audienceFacade;
  }

  @Override
  public void deliverMessage(
      final Player target, final MutableMessage message
  ) {
    audienceFacade
        .getAudienceByUniqueId(target.getUniqueId())
        .thenAccept(messageViewer -> {
          if (messageViewer.isAllowsMessages()) {
            target.sendMessage(message.compile());
          }
        });
  }

  @Override
  public void deliverMessage(
      final Collection<? extends Player> targets, final MutableMessage message
  ) {
    targets.forEach(target -> deliverMessage(target, message));
  }
}
