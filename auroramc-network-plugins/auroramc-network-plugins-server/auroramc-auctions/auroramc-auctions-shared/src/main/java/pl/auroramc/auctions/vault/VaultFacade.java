package pl.auroramc.auctions.vault;

import java.util.concurrent.CompletableFuture;

public interface VaultFacade {

  CompletableFuture<Vault> getVaultByUserId(final Long userId);

  CompletableFuture<Void> createVault(final Vault vault);

  CompletableFuture<Void> deleteVault(final Vault vault);
}
