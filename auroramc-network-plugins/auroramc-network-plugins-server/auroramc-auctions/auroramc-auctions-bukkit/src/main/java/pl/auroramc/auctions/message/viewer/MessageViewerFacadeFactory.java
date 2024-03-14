package pl.auroramc.auctions.message.viewer;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class MessageViewerFacadeFactory {

  private MessageViewerFacadeFactory() {

  }

  public static MessageViewerFacade getMessageViewerFacade(
      final Logger logger, final Juliet juliet
  ) {
    final SqlMessageViewerRepository sqlMessageViewerRepository = new SqlMessageViewerRepository(juliet);
    sqlMessageViewerRepository.createMessageViewerSchemaIfRequired();
    return new MessageViewerService(logger, sqlMessageViewerRepository);
  }
}
