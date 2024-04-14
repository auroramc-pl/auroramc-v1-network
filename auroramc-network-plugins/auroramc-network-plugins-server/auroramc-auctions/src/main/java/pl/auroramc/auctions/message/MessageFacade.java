package pl.auroramc.auctions.message;

import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public interface MessageFacade {

  static MessageFacade getMessageFacade(
      final BukkitMessageCompiler messageCompiler, final AudienceFacade audienceFacade) {
    return new MessageService(messageCompiler, audienceFacade);
  }

  void deliverMessage(final MutableMessage message);
}
