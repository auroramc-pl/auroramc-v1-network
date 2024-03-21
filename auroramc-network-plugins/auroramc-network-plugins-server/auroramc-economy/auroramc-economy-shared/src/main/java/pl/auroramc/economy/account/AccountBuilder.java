package pl.auroramc.economy.account;

import java.math.BigDecimal;

public final class AccountBuilder {

  private Long userId;
  private Long currencyId;
  private BigDecimal balance;

  private AccountBuilder() {}

  public static AccountBuilder newBuilder() {
    return new AccountBuilder();
  }

  public AccountBuilder withUserId(final Long userId) {
    this.userId = userId;
    return this;
  }

  public AccountBuilder withCurrencyId(final Long currencyId) {
    this.currencyId = currencyId;
    return this;
  }

  public AccountBuilder withBalance(final BigDecimal balance) {
    this.balance = balance;
    return this;
  }

  public Account build() {
    return new Account(null, userId, currencyId, balance);
  }
}
