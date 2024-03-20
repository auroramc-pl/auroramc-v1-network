package pl.auroramc.auctions.vault;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class VaultService implements VaultFacade {

  private final Logger logger;
  private final VaultRepository vaultRepository;
  private final AsyncLoadingCache<Long, Vault> vaultCache;

  VaultService(final Logger logger, final VaultRepository vaultRepository) {
    this.logger = logger;
    this.vaultRepository = vaultRepository;
    this.vaultCache =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(vaultRepository::getVaultByUserId);
  }

  @Override
  public CompletableFuture<Vault> getVaultByUserId(final Long userId) {
    return vaultCache.get(userId);
  }

  @Override
  public CompletableFuture<Void> createVault(final Vault vault) {
    return runAsync(() -> vaultRepository.createVault(vault))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> deleteVault(final Vault vault) {
    return runAsync(() -> vaultRepository.deleteVault(vault))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
