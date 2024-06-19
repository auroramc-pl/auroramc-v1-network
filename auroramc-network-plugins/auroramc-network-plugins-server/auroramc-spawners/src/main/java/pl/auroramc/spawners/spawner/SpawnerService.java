package pl.auroramc.spawners.spawner;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static pl.auroramc.commons.eager.Eager.eager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import pl.auroramc.commons.eager.Eager;
import pl.auroramc.integrations.dsl.BukkitDiscoveryService;
import pl.auroramc.spawners.spawner.SpawnerDsl.SpawnersDsl;

class SpawnerService extends BukkitDiscoveryService<SpawnersDsl> implements SpawnerFacade {

  private final Eager<Set<Spawner>> spawners;

  SpawnerService(final ClassLoader parentClassLoader, final Path spawnerDefinitionsPath) {
    super(parentClassLoader, SpawnersDsl.class);
    this.spawners =
        eager(
            () ->
                getElementDefinitions(spawnerDefinitionsPath).stream()
                    .map(SpawnersDsl::spawners)
                    .flatMap(List::stream)
                    .collect(toUnmodifiableSet()));
  }

  @Override
  public Set<Spawner> getSpawners() {
    return spawners.get();
  }
}
