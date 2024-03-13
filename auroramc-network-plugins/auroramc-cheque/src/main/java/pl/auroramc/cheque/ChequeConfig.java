package pl.auroramc.cheque;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class ChequeConfig extends OkaeriConfig {

  public static final @Exclude String PLUGIN_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;
}
