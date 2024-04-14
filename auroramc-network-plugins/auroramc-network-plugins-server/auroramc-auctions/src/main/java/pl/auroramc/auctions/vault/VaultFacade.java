package pl.auroramc.auctions.vault;

import java.util.concurrent.CompletableFuture;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public interface VaultFacade {

  static VaultFacade getVaultFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlVaultRepository sqlVaultRepository = new SqlVaultRepository(juliet);
    sqlVaultRepository.createVaultSchemaIfRequired();
    return new VaultService(scheduler, sqlVaultRepository);
  }

  CompletableFuture<Vault> getVaultByUserId(final Long userId);

  CompletableFuture<Void> createVault(final Vault vault);

  CompletableFuture<Void> deleteVault(final Vault vault);
}
