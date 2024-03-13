package pl.auroramc.quests.resource.key;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import pl.auroramc.quests.resource.Resource;

class ResourceKeyService implements ResourceKeyFacade {

  private final ResourceKeyRepository resourceKeyRepository;

  ResourceKeyService(final ResourceKeyRepository resourceKeyRepository) {
    this.resourceKeyRepository = resourceKeyRepository;
  }

  @Override
  public List<ResourceKey> getResourceKeys() {
    return resourceKeyRepository.getResourceKeys();
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
  public void validateResourceKeys(final List<? extends Resource> resources) {
    final List<String> localNamesOfResourceKeys = getNamesOfResourceKeys(resources);
    createResourceKeys(getResourceKeysToCreate(localNamesOfResourceKeys));
    deleteResourceKeys(getResourceKeysToDelete(localNamesOfResourceKeys));
    assignIdsOfResourceKeys(resources);
  }

  private void assignIdsOfResourceKeys(final List<? extends Resource> resources) {
    final Map<String, ResourceKey> allResourceKeys = getResourceKeys().stream()
        .collect(toMap(ResourceKey::getName, identity()));
    assignIdsOfResourceKeys(allResourceKeys, resources);
    assignIdsOfResourceKeys(allResourceKeys, resources.stream()
        .map(Resource::children)
        .flatMap(List::stream)
        .toList());
  }

  private void assignIdsOfResourceKeys(
      final Map<String, ResourceKey> allResourceKeys, final List<? extends Resource> resources
  ) {
    for (final Resource resource : resources) {
      final ResourceKey resourceKey = resource.getKey();
      final Long idOfResourceKey = allResourceKeys.get(resourceKey.getName()).getId();
      resourceKey.setId(idOfResourceKey);
    }
  }

  private List<String> getNamesOfResourceKeys(final List<? extends Resource> resources) {
    final List<? extends Resource> childrenOfResources = resources.stream().map(Resource::children)
        .flatMap(List::stream)
        .toList();
    final List<? extends Resource> aggregatedResources = Stream.of(resources, childrenOfResources)
        .flatMap(Collection::stream)
        .toList();
    return aggregatedResources.stream()
        .map(Resource::getKey)
        .map(ResourceKey::getName)
        .toList();
  }

  private List<ResourceKey> getResourceKeysToCreate(final List<String> localNamesOfQuestKeys) {
    final List<ResourceKey> allResourceKeys = getResourceKeys();
    return localNamesOfQuestKeys.stream()
        .distinct()
        .filter(nameOfResourceKey -> allResourceKeys.stream()
            .map(ResourceKey::getName)
            .noneMatch(nameOfResourceKey::equals))
        .map(name -> new ResourceKey(null, name))
        .toList();
  }

  private List<ResourceKey> getResourceKeysToDelete(final List<String> localNamesOfResourceKeys) {
    final List<ResourceKey> allResourceKeys = getResourceKeys();
    return allResourceKeys.stream()
        .filter(not(questKey -> localNamesOfResourceKeys.contains(questKey.getName())))
        .toList();
  }
}
