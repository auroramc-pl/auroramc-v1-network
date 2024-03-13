package pl.auroramc.scoreboard;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class ScoreboardConfig extends OkaeriConfig {

  public static final @Exclude String PLUGIN_CONFIG_FILE_NAME = "config.yml";

  public boolean renderOnce = true;
}
