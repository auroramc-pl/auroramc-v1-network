package pl.auroramc.auctions.vault.item;

import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class VaultItemService implements VaultItemFacade {

  private final Logger logger;
  private final VaultItemRepository vaultItemRepository;
  private final LoadingCache<Long, Set<VaultItem>> vaultItemCache;

  VaultItemService(final Logger logger, final VaultItemRepository vaultItemRepository) {
    this.logger = logger;
    this.vaultItemRepository = vaultItemRepository;
    this.vaultItemCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
        .build(vaultItemRepository::findVaultItemsByUserId);
  }

  @Override
  public Set<VaultItem> getVaultItemsByUserId(final Long userId) {
    return vaultItemCache.get(userId);
  }

  @Override
  public CompletableFuture<Void> createVaultItem(final VaultItem vaultItem) {
    return runAsync(() -> vaultItemCache.invalidate(vaultItem.getVaultId()))
        .thenAccept(state -> vaultItemRepository.createVaultItem(vaultItem))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem) {
    return runAsync(() -> vaultItemRepository.deleteVaultItem(vaultItem))
        .thenAccept(state -> vaultItemCache.invalidate(vaultItem.getVaultId()))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
