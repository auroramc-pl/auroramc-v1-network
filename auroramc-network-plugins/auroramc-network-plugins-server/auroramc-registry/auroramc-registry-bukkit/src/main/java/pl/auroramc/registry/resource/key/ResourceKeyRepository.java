package pl.auroramc.registry.resource.key;

import java.util.List;

interface ResourceKeyRepository {

  List<ResourceKey> getResourceKeysByProviderId(final Long providerId);

  void createResourceKeys(final List<ResourceKey> resourceKeys);

  void deleteResourceKeys(final List<ResourceKey> resourceKeys);
}
