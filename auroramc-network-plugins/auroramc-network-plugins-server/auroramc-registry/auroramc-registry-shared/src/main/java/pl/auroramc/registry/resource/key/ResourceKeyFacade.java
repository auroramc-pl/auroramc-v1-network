package pl.auroramc.registry.resource.key;

import java.util.List;
import pl.auroramc.registry.provider.Provider;
import pl.auroramc.registry.resource.Resource;

public interface ResourceKeyFacade {

  List<ResourceKey> getResourceKeysByProviderId(final Long providerId);

  void createResourceKeys(final List<ResourceKey> resourceKeys);

  void deleteResourceKeys(final List<ResourceKey> resourceKeys);

  void validateResourceKeys(final Provider provider, final List<? extends Resource> resources);
}
