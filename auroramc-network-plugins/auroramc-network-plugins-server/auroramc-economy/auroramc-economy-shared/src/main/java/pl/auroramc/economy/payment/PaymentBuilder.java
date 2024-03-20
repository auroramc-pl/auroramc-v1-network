package pl.auroramc.economy.payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentBuilder {

  private Long initiatorId;
  private Long receiverId;
  private Long currencyId;
  private BigDecimal amount;
  private Instant transactionTime;

  private PaymentBuilder() {}

  public static PaymentBuilder newBuilder() {
    return new PaymentBuilder();
  }

  public PaymentBuilder withInitiatorId(final Long initiatorId) {
    this.initiatorId = initiatorId;
    return this;
  }

  public PaymentBuilder withReceiverId(final Long receiverId) {
    this.receiverId = receiverId;
    return this;
  }

  public PaymentBuilder withCurrencyId(final Long currencyId) {
    this.currencyId = currencyId;
    return this;
  }

  public PaymentBuilder withAmount(final BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  public PaymentBuilder withTransactionTime(final Instant transactionTime) {
    this.transactionTime = transactionTime;
    return this;
  }

  public Payment build() {
    return new Payment(null, initiatorId, receiverId, currencyId, amount, transactionTime);
  }
}
