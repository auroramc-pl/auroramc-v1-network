package pl.auroramc.auctions.message;


import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;

public interface MessageFacade {

  static MessageFacade getMessageFacade(final AudienceFacade audienceFacade) {
    return new MessageService(audienceFacade);
  }

  void deliverMessage(final DeliverableMutableMessage message);
}
