package pl.auroramc.registry.provider;

import moe.rafal.juliet.Juliet;

public final class ProviderFacadeFactory {

  private ProviderFacadeFactory() {}

  public static ProviderFacade getProviderFacade(final Juliet juliet) {
    final SqlProviderRepository providerRepository = new SqlProviderRepository(juliet);
    providerRepository.createProviderSchemaIfRequired();
    return new ProviderService(providerRepository);
  }
}
