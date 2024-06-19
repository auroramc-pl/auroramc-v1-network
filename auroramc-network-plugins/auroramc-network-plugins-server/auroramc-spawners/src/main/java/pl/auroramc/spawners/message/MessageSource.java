package pl.auroramc.spawners.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.integrations.configs.command.CommandMessageSource;
import pl.auroramc.spawners.spawner.SpawnerMessageSource;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public CommandMessageSource command = new CommandMessageSource();

  public SpawnerMessageSource spawner = new SpawnerMessageSource();
}
