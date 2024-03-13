package pl.auroramc.dailyrewards;

import static java.time.Duration.ofSeconds;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.time.Duration;

public class DailyRewardsConfig extends OkaeriConfig {

  public static final @Exclude String PLUGIN_CONFIG_FILE_NAME = "config.yml";

  public Duration visitBuffer = ofSeconds(30);
}
