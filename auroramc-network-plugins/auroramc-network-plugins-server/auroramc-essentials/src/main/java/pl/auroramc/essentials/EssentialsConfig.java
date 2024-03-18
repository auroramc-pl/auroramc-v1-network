package pl.auroramc.essentials;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class EssentialsConfig extends OkaeriConfig {

  public static final @Exclude String PLUGIN_CONFIG_FILE_NAME = "config.yml";

  public double prefixScale = 0.1;

  public int prefixLength = 4;

  public double minimalScoreForCommandSuggestion = 0.75;
}
