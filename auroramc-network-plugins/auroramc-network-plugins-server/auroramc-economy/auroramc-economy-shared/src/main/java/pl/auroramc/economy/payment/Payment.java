package pl.auroramc.economy.payment;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {

  private Long id;
  private Long initiatorId;
  private Long receiverId;
  private Long currencyId;
  private BigDecimal amount;
  private Instant transactionTime;

  public Payment(
      final Long id,
      final Long initiatorId,
      final Long receiverId,
      final Long currencyId,
      final BigDecimal amount,
      final Instant transactionTime
  ) {
    this.id = id;
    this.initiatorId = initiatorId;
    this.receiverId = receiverId;
    this.currencyId = currencyId;
    this.amount = amount;
    this.transactionTime = transactionTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getInitiatorId() {
    return initiatorId;
  }

  public void setInitiatorId(final Long initiatorId) {
    this.initiatorId = initiatorId;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(final Long receiverId) {
    this.receiverId = receiverId;
  }

  public Long getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(final Long currencyId) {
    this.currencyId = currencyId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
  }

  public Instant getTransactionTime() {
    return transactionTime;
  }

  public void setTransactionTime(final Instant transactionTime) {
    this.transactionTime = transactionTime;
  }
}
