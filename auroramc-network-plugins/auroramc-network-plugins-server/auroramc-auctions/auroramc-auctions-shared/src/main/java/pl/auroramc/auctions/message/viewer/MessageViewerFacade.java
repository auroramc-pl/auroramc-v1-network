package pl.auroramc.auctions.message.viewer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MessageViewerFacade {

  CompletableFuture<MessageViewer> getMessageViewerByUserUniqueId(final UUID uniqueId);

  CompletableFuture<Void> createMessageViewer(final MessageViewer messageViewer);

  CompletableFuture<Void> updateMessageViewer(final MessageViewer messageViewer);
}
