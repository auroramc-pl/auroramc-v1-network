package pl.auroramc.economy.payment;

import eu.okaeri.configs.OkaeriConfig;
import java.math.BigDecimal;

public class PaymentConfig extends OkaeriConfig {

  public BigDecimal paymentBuffer = BigDecimal.valueOf(5_000);
}
