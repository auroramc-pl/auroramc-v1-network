package pl.auroramc.auctions.vault;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class VaultFacadeFactory {

  private VaultFacadeFactory() {

  }

  public static VaultFacade getVaultFacade(final Logger logger, final Juliet juliet) {
    final SqlVaultRepository sqlVaultRepository = new SqlVaultRepository(juliet);
    sqlVaultRepository.createVaultSchemaIfRequired();
    return new VaultService(logger, sqlVaultRepository);
  }
}
