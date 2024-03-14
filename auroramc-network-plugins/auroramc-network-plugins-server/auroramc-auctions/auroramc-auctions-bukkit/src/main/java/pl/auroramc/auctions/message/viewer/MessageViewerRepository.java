package pl.auroramc.auctions.message.viewer;

import java.util.UUID;

public interface MessageViewerRepository {

  MessageViewer findMessageViewerByUserUniqueId(final UUID uniqueId);

  void createMessageViewer(final MessageViewer messageViewer);

  void updateMessageViewer(final MessageViewer messageViewer);
}
