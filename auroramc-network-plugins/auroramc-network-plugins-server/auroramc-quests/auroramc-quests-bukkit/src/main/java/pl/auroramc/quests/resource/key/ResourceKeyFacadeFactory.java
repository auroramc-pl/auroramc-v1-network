package pl.auroramc.quests.resource.key;

import moe.rafal.juliet.Juliet;

public final class ResourceKeyFacadeFactory {

  private ResourceKeyFacadeFactory() {}

  public static ResourceKeyFacade getResourceKeyFacade(final Juliet juliet) {
    final SqlResourceKeyRepository sqlResourceKeyRepository = new SqlResourceKeyRepository(juliet);
    sqlResourceKeyRepository.createResourceKeySchemaIfRequired();
    return new ResourceKeyService(sqlResourceKeyRepository);
  }
}
