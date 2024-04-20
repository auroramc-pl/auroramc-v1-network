package pl.auroramc.auctions.vault.item;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public interface VaultItemFacade {

  static VaultItemFacade getVaultItemFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlVaultItemRepository sqlVaultItemRepository = new SqlVaultItemRepository(juliet);
    sqlVaultItemRepository.createVaultItemSchemaIfRequired();
    return new VaultItemService(scheduler, sqlVaultItemRepository);
  }

  Set<VaultItem> getVaultItemsByUserId(final Long userId);

  CompletableFuture<Void> createVaultItem(final VaultItem vaultItem);

  CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem);
}
