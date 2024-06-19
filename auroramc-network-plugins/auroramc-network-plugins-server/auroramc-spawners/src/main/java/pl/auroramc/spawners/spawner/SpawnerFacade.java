package pl.auroramc.spawners.spawner;

import java.nio.file.Path;
import java.util.Set;

public interface SpawnerFacade {

  static SpawnerFacade getSpawnerFacade(
      final ClassLoader pluginClassLoader, final Path spawnerDefinitionsPath) {
    return new SpawnerService(pluginClassLoader, spawnerDefinitionsPath);
  }

  Set<Spawner> getSpawners();
}
