package pl.auroramc.gamble;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class GambleConfig extends OkaeriConfig {

  public static final @Exclude String GAMBLING_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;
}
