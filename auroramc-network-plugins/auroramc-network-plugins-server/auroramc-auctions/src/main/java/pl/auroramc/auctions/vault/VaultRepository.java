package pl.auroramc.auctions.vault;

public interface VaultRepository {

  Vault getVaultByUserId(final Long userId);

  void createVault(final Vault vault);

  void deleteVault(final Vault vault);
}
