package pl.auroramc.registry.resource.provider;

import moe.rafal.juliet.Juliet;

public final class ResourceProviderFacadeFactory {

  private ResourceProviderFacadeFactory() {}

  public static ResourceProviderFacade getResourceProviderFacade(final Juliet juliet) {
    final SqlResourceProviderRepository resourceProviderRepository =
        new SqlResourceProviderRepository(juliet);
    resourceProviderRepository.createResourceProviderSchemaIfRequired();
    return new ResourceProviderService(resourceProviderRepository);
  }
}
