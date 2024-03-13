package pl.auroramc.economy.account;

import java.math.BigDecimal;

final class SqlAccountRepositoryUtils {

  private SqlAccountRepositoryUtils() {

  }

  static int getDigitCountBeforeDecimalPoint(final BigDecimal value) {
    return value.toBigInteger().toString().length();
  }
}
