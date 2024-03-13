package pl.auroramc.economy.account;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Account {

  private final Lock lockForWrite;
  private Long userId;
  private Long currencyId;
  private BigDecimal balance;

  Account(final Long userId, final Long currencyId, final BigDecimal balance) {
    final ReadWriteLock lock = new ReentrantReadWriteLock();
    this.lockForWrite = lock.writeLock();
    this.userId = userId;
    this.currencyId = currencyId;
    this.balance = balance;
  }

  public Lock getLockForWrite() {
    return lockForWrite;
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
    return
        Objects.equals(userId, account.userId) &&
        Objects.equals(currencyId, account.currencyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, currencyId);
  }
}
