package pl.auroramc.auctions.vault.item;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface VaultItemFacade {

  Set<VaultItem> getVaultItemsByUserId(final Long userId);

  CompletableFuture<Void> createVaultItem(final VaultItem vaultItem);

  CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem);
}
