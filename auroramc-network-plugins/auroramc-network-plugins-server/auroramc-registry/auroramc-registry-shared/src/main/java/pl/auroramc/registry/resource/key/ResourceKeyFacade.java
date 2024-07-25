package pl.auroramc.registry.resource.key;

import java.util.List;
import pl.auroramc.registry.resource.Resource;
import pl.auroramc.registry.resource.provider.ResourceProvider;

public interface ResourceKeyFacade {

  List<ResourceKey> getResourceKeysByProviderId(final Long providerId);

  void createResourceKeys(final List<ResourceKey> resourceKeys);

  void deleteResourceKeys(final List<ResourceKey> resourceKeys);

  void validateResourceKeys(
      final ResourceProvider resourceProvider, final List<? extends Resource> resources);
}
