package pl.auroramc.spawners.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.spawners.spawner.SpawnerMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public SpawnerMessageSource spawner = new SpawnerMessageSource();
}
