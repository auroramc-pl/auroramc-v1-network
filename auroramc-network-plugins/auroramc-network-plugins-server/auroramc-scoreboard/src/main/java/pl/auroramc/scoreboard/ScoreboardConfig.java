package pl.auroramc.scoreboard;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.util.List;

public class ScoreboardConfig extends OkaeriConfig {

  public static final @Exclude String SCOREBOARD_CONFIG_FILE_NAME = "config.yml";

  public boolean updatePeriodically = true;

  public List<String> lines = List.of("lineWithName", "lineWithGroup");
}
