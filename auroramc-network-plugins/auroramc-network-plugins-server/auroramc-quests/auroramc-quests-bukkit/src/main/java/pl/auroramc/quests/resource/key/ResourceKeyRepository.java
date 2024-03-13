package pl.auroramc.quests.resource.key;

import java.util.List;

interface ResourceKeyRepository {

  List<ResourceKey> getResourceKeys();

  void createResourceKeys(final List<ResourceKey> resourceKeys);

  void deleteResourceKeys(final List<ResourceKey> resourceKeys);
}
