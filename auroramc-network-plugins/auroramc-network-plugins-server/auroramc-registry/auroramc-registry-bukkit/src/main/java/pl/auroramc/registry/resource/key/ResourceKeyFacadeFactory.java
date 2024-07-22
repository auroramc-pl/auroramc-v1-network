package pl.auroramc.registry.resource.key;

import moe.rafal.juliet.Juliet;

public final class ResourceKeyFacadeFactory {

  private ResourceKeyFacadeFactory() {}

  public static ResourceKeyFacade getResourceKeyFacade(final Juliet juliet) {
    final SqlResourceKeyRepository resourceKeyRepository = new SqlResourceKeyRepository(juliet);
    resourceKeyRepository.createResourceKeysSchemaIfRequired();
    return new ResourceKeyService(resourceKeyRepository);
  }
}
