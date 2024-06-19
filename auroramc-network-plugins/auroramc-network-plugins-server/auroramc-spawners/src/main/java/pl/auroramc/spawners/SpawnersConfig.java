package pl.auroramc.spawners;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class SpawnersConfig extends OkaeriConfig {

  public static final @Exclude String SPAWNERS_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;

  public int spawnerReacquirePercentage = 50;
}
