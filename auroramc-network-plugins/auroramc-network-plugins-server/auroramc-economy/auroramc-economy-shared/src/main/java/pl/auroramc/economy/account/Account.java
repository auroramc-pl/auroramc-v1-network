package pl.auroramc.economy.account;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

public class Account {

  private final StampedLock lock = new StampedLock();
  private Long id;
  private Long userId;
  private Long currencyId;
  private BigDecimal balance;

  Account(final Long id, final Long userId, final Long currencyId, final BigDecimal balance) {
    this.id = id;
    this.userId = userId;
    this.currencyId = currencyId;
    this.balance = balance;
  }

  public StampedLock getLock() {
    return lock;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public Long getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(final Long currencyId) {
    this.currencyId = currencyId;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(final BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(final Object comparedObject) {
    if (this == comparedObject) {
      return true;
    }

    if (comparedObject == null || getClass() != comparedObject.getClass()) {
      return false;
    }

    final Account account = (Account) comparedObject;
    return Objects.equals(userId, account.userId) && Objects.equals(currencyId, account.currencyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, currencyId);
  }
}
