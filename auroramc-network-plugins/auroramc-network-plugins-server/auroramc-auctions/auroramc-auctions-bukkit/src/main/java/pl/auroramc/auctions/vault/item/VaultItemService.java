package pl.auroramc.auctions.vault.item;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class VaultItemService implements VaultItemFacade {

  private final Logger logger;
  private final VaultItemRepository vaultItemRepository;
  private final LoadingCache<Long, Set<VaultItem>> vaultItemsByUniqueId;

  VaultItemService(final Logger logger, final VaultItemRepository vaultItemRepository) {
    this.logger = logger;
    this.vaultItemRepository = vaultItemRepository;
    this.vaultItemsByUniqueId = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(30))
        .build(vaultItemRepository::findVaultItemsByUserId);
  }

  @Override
  public Set<VaultItem> getVaultItemsByUserId(final Long userId) {
    return vaultItemsByUniqueId.get(userId);
  }

  @Override
  public CompletableFuture<Void> createVaultItem(final VaultItem vaultItem) {
    return runAsync(() -> vaultItemsByUniqueId.invalidate(vaultItem.getUserId()))
        .thenAccept(state -> vaultItemRepository.createVaultItem(vaultItem))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem) {
    return runAsync(() -> vaultItemsByUniqueId.invalidate(vaultItem.getUserId()))
        .thenAccept(state -> vaultItemRepository.deleteVaultItem(vaultItem))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
