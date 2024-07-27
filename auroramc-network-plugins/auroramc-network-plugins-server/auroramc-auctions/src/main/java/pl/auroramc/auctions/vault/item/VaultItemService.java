package pl.auroramc.auctions.vault.item;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.concurrent.CompletableFutureUtils.delegateCaughtException;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;
import pl.auroramc.commons.sql.NoRowsAffectedException;

class VaultItemService implements VaultItemFacade {

  private final Logger logger;
  private final Scheduler scheduler;
  private final VaultItemRepository vaultItemRepository;
  private final LoadingCache<Long, List<VaultItem>> vaultItemsByUniqueId;

  VaultItemService(
      final Logger logger,
      final Scheduler scheduler,
      final VaultItemRepository vaultItemRepository) {
    this.logger = logger;
    this.scheduler = scheduler;
    this.vaultItemRepository = vaultItemRepository;
    this.vaultItemsByUniqueId =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .build(vaultItemRepository::findVaultItemsByUserId);
  }

  @Override
  public List<VaultItem> getVaultItemsByUserId(final Long userId) {
    return vaultItemsByUniqueId.get(userId);
  }

  @Override
  public CompletableFuture<Void> createVaultItem(final VaultItem vaultItem) {
    return scheduler
        .run(ASYNC, () -> vaultItemsByUniqueId.invalidate(vaultItem.getUserId()))
        .thenAccept(state -> vaultItemRepository.createVaultItem(vaultItem));
  }

  @Override
  public CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem) {
    return scheduler
        .run(ASYNC, () -> vaultItemsByUniqueId.invalidate(vaultItem.getUserId()))
        .thenAccept(state -> vaultItemRepository.deleteVaultItem(vaultItem))
        .exceptionally(exception -> handleVaultItemDeletionFailure(vaultItem, exception));
  }

  private Void handleVaultItemDeletionFailure(
      final VaultItem vaultItem, final Throwable exception) {
    if (exception instanceof CompletionException completionException
        && completionException.getCause()
            instanceof VaultItemRepositoryException repositoryException
        && repositoryException.getCause() instanceof NoRowsAffectedException) {
      final String message =
          "Could not delete vault item identified by %d owned by %d, because query did not affected any rows."
              .formatted(vaultItem.getId(), vaultItem.getUserId());
      logger.warning(message);
      throw new VaultItemDeletionException(message, exception);
    }

    return delegateCaughtException(exception);
  }
}
