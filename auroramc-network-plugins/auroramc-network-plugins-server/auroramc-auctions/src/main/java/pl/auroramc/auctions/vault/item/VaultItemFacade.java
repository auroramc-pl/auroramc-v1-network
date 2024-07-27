package pl.auroramc.auctions.vault.item;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public interface VaultItemFacade {

  static VaultItemFacade getVaultItemFacade(final Logger logger, final Scheduler scheduler, final Juliet juliet) {
    final SqlVaultItemRepository sqlVaultItemRepository = new SqlVaultItemRepository(juliet);
    sqlVaultItemRepository.createVaultItemSchemaIfRequired();
    return new VaultItemService(logger, scheduler, sqlVaultItemRepository);
  }

  List<VaultItem> getVaultItemsByUserId(final Long userId);

  CompletableFuture<Void> createVaultItem(final VaultItem vaultItem);

  CompletableFuture<Void> deleteVaultItem(final VaultItem vaultItem);
}
