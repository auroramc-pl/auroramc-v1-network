package pl.auroramc.commons.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.configurer.Configurer;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ConfigFactory {

  private static final boolean WHETHER_CONFIGURATION_SHOULD_BE_UP_TO_DATE = true;
  private final Path dataPath;
  private final Supplier<Configurer> configurer;

  public ConfigFactory(final Path dataPath, final Supplier<Configurer> configurer) {
    this.dataPath = dataPath;
    this.configurer = configurer;
  }

  public <T extends OkaeriConfig> T produceConfig(
      final Class<T> configClass,
      final String configFileName,
      final OkaeriSerdesPack... serdesPacks) {
    return produceConfig(configClass, dataPath.resolve(configFileName), serdesPacks);
  }

  public <T extends OkaeriConfig> T produceConfig(
      final Class<T> configClass,
      final Path configFilePath,
      final OkaeriSerdesPack... serdesPacks) {
    return ConfigManager.create(
        configClass,
        initializer ->
            initializer
                .withConfigurer(configurer.get(), serdesPacks)
                .withBindFile(configFilePath)
                .saveDefaults()
                .load(WHETHER_CONFIGURATION_SHOULD_BE_UP_TO_DATE));
  }
}
