package pl.auroramc.auctions.message.viewer;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class MessageViewerService implements MessageViewerFacade {

  private final Logger logger;
  private final MessageViewerRepository messageViewerRepository;
  private final AsyncLoadingCache<UUID, MessageViewer> messageViewerCache;

  MessageViewerService(
      final Logger logger, final MessageViewerRepository messageViewerRepository
  ) {
    this.logger = logger;
    this.messageViewerRepository = messageViewerRepository;
    this.messageViewerCache = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(20))
        .buildAsync(messageViewerRepository::findMessageViewerByUserUniqueId);
  }

  @Override
  public CompletableFuture<MessageViewer> getMessageViewerByUserUniqueId(final UUID uniqueId) {
    return messageViewerCache.get(uniqueId);
  }

  @Override
  public CompletableFuture<Void> createMessageViewer(final MessageViewer messageViewer) {
    return runAsync(() -> messageViewerRepository.createMessageViewer(messageViewer))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> updateMessageViewer(final MessageViewer messageViewer) {
    return runAsync(() -> messageViewerRepository.updateMessageViewer(messageViewer))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
