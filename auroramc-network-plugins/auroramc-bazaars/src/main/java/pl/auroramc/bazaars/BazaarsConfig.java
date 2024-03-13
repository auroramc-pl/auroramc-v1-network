package pl.auroramc.bazaars;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.text.DecimalFormat;

public class BazaarsConfig extends OkaeriConfig {

  @Exclude
  public static final String BAZAARS_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;

  public DecimalFormat priceFormat = new DecimalFormat("#,###.##");
}
