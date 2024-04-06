package pl.auroramc.bazaars;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class BazaarsConfig extends OkaeriConfig {

  public static final @Exclude String BAZAARS_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;
}
