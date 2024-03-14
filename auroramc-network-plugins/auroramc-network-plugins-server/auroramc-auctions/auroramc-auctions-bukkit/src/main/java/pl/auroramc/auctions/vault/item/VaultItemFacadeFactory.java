package pl.auroramc.auctions.vault.item;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class VaultItemFacadeFactory {

  private VaultItemFacadeFactory() {

  }

  public static VaultItemFacade getVaultItemFacade(final Logger logger, final Juliet juliet) {
    final SqlVaultItemRepository sqlVaultItemRepository = new SqlVaultItemRepository(juliet);
    sqlVaultItemRepository.createVaultItemSchemaIfRequired();
    return new VaultItemService(logger, sqlVaultItemRepository);
  }
}
