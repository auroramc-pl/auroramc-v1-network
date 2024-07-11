package pl.auroramc.auctions.vault.item;

import java.util.List;

public interface VaultItemRepository {

  List<VaultItem> findVaultItemsByUserId(final Long userId);

  void createVaultItem(final VaultItem vaultItem);

  void deleteVaultItem(final VaultItem vaultItem);
}
