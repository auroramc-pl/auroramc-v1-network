package pl.auroramc.registry.resource.key;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import pl.auroramc.registry.provider.Provider;
import pl.auroramc.registry.resource.Resource;

class ResourceKeyService implements ResourceKeyFacade {

  private final ResourceKeyRepository resourceKeyRepository;

  ResourceKeyService(final ResourceKeyRepository resourceKeyRepository) {
    this.resourceKeyRepository = resourceKeyRepository;
  }

  @Override
  public List<ResourceKey> getResourceKeysByProviderId(final Long providerId) {
    return resourceKeyRepository.getResourceKeysByProviderId(providerId);
  }

  @Override
  public void createResourceKeys(final List<ResourceKey> resourceKeys) {
    resourceKeyRepository.createResourceKeys(resourceKeys);
  }

  @Override
  public void deleteResourceKeys(final List<ResourceKey> resourceKeys) {
    resourceKeyRepository.deleteResourceKeys(resourceKeys);
  }

  @Override
  public void validateResourceKeys(
      final Provider provider, final List<? extends Resource> resources) {
    final List<String> localNamesOfResourceKeys = getNamesOfResourceKeys(resources);
    createResourceKeys(getResourceKeysToCreate(provider, localNamesOfResourceKeys));
    deleteResourceKeys(getResourceKeysToDelete(provider, localNamesOfResourceKeys));
    assignIdsOfResourceKeys(provider, resources);
  }

  private void assignIdsOfResourceKeys(
      final Provider provider, final List<? extends Resource> resources) {
    final Map<String, ResourceKey> allResourceKeys =
        getResourceKeysByProviderId(provider.getId()).stream()
            .collect(toMap(ResourceKey::getName, identity()));
    assignIdsOfResourceKeys(allResourceKeys, resources);
    assignIdsOfResourceKeys(
        allResourceKeys, resources.stream().map(Resource::children).flatMap(List::stream).toList());
  }

  private void assignIdsOfResourceKeys(
      final Map<String, ResourceKey> allResourceKeys, final List<? extends Resource> resources) {
    for (final Resource resource : resources) {
      final ResourceKey resourceKey = resource.getKey();
      final Long idOfResourceKey = allResourceKeys.get(resourceKey.getName()).getId();
      resourceKey.setId(idOfResourceKey);
    }
  }

  private List<String> getNamesOfResourceKeys(final List<? extends Resource> resources) {
    final List<? extends Resource> childrenOfResources =
        resources.stream().map(Resource::children).flatMap(List::stream).toList();
    final List<? extends Resource> aggregatedResources =
        Stream.of(resources, childrenOfResources).flatMap(Collection::stream).toList();
    return aggregatedResources.stream().map(Resource::getKey).map(ResourceKey::getName).toList();
  }

  private List<ResourceKey> getResourceKeysToCreate(
      final Provider provider, final List<String> localNamesOfResourceKeys) {
    final List<ResourceKey> allResourceKeys = getResourceKeysByProviderId(provider.getId());
    return localNamesOfResourceKeys.stream()
        .distinct()
        .filter(
            nameOfResourceKey ->
                allResourceKeys.stream()
                    .map(ResourceKey::getName)
                    .noneMatch(nameOfResourceKey::equals))
        .map(
            name ->
                ResourceKeyBuilder.newBuilder()
                    .withProviderId(provider.getId())
                    .withName(name)
                    .build())
        .toList();
  }

  private List<ResourceKey> getResourceKeysToDelete(
      final Provider provider, final List<String> localNamesOfResourceKeys) {
    final List<ResourceKey> allResourceKeys = getResourceKeysByProviderId(provider.getId());
    return allResourceKeys.stream()
        .filter(not(resourceKey -> localNamesOfResourceKeys.contains(resourceKey.getName())))
        .toList();
  }
}
