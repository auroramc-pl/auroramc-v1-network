package pl.auroramc.auctions.vault.item;

import java.util.Set;

public interface VaultItemRepository {

  Set<VaultItem> findVaultItemsByUserId(final Long userId);

  void createVaultItem(final VaultItem vaultItem);

  void deleteVaultItem(final VaultItem vaultItem);
}
