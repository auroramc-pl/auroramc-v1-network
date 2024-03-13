package pl.auroramc.shops;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.text.DecimalFormat;

public class ShopsConfig extends OkaeriConfig {

  public static final @Exclude String SHOPS_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;

  public DecimalFormat priceFormat = new DecimalFormat("#,###.##");
}
