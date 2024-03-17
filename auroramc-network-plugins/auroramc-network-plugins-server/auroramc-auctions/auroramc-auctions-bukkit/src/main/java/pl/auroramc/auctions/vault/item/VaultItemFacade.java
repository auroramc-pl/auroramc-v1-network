package pl.auroramc.auctions.vault.item;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public interface VaultItemFacade {

  static VaultItemFacade getVaultItemFacade(final Logger logger, final Juliet juliet) {
    final SqlVaultItemRepository sqlVaultItemRepository = new SqlVaultItemRepository(juliet);
    sqlVaultItemRepository.createVaultItemSchemaIfRequired();
    return new VaultItemService(logger, sqlVaultItemRepository);
  }

  Set<VaultItem> getVaultItemsByUserId(final Long userId);

  CompletableFuture<Void> createVaultItem(final VaultItem vaultItem);

  CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem);
}
