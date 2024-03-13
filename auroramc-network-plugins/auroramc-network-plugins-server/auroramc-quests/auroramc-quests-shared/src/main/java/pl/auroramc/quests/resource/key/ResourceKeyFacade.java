package pl.auroramc.quests.resource.key;

import java.util.List;
import pl.auroramc.quests.resource.Resource;

public interface ResourceKeyFacade {

  List<ResourceKey> getResourceKeys();

  void createResourceKeys(final List<ResourceKey> resourceKeys);

  void deleteResourceKeys(final List<ResourceKey> resourceKeys);

  void validateResourceKeys(final List<? extends Resource> resources);
}
