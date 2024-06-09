package pl.auroramc.auctions.vault;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class VaultService implements VaultFacade {

  private final Scheduler scheduler;
  private final VaultRepository vaultRepository;
  private final AsyncLoadingCache<Long, Vault> vaultCache;

  VaultService(final Scheduler scheduler, final VaultRepository vaultRepository) {
    this.scheduler = scheduler;
    this.vaultRepository = vaultRepository;
    this.vaultCache =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(vaultRepository::getVaultByUserId);
  }

  @Override
  public CompletableFuture<Vault> getVaultByUserId(final Long userId) {
    return vaultCache.get(userId);
  }

  @Override
  public CompletableFuture<Void> createVault(final Vault vault) {
    return scheduler
        .run(ASYNC, () -> vaultRepository.createVault(vault))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> deleteVault(final Vault vault) {
    return scheduler
        .run(ASYNC, () -> vaultRepository.deleteVault(vault))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
