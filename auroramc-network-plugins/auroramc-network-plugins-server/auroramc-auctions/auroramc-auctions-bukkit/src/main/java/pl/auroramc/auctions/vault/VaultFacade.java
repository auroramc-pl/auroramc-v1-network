package pl.auroramc.auctions.vault;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public interface VaultFacade {

  static VaultFacade getVaultFacade(final Logger logger, final Juliet juliet) {
    final SqlVaultRepository sqlVaultRepository = new SqlVaultRepository(juliet);
    sqlVaultRepository.createVaultSchemaIfRequired();
    return new VaultService(logger, sqlVaultRepository);
  }

  CompletableFuture<Vault> getVaultByUserId(final Long userId);

  CompletableFuture<Void> createVault(final Vault vault);

  CompletableFuture<Void> deleteVault(final Vault vault);
}
