package pl.auroramc.auctions.vault.item;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class VaultItemService implements VaultItemFacade {

  private final Scheduler scheduler;
  private final VaultItemRepository vaultItemRepository;
  private final LoadingCache<Long, List<VaultItem>> vaultItemsByUniqueId;

  VaultItemService(final Scheduler scheduler, final VaultItemRepository vaultItemRepository) {
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
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
