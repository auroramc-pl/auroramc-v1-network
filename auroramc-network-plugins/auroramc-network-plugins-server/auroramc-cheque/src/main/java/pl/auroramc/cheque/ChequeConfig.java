package pl.auroramc.cheque;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.math.BigDecimal;

public class ChequeConfig extends OkaeriConfig {

  public static final @Exclude String CHEQUE_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;

  public BigDecimal paymentBuffer = new BigDecimal(1_000);
}
