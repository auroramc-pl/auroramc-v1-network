package pl.auroramc.economy.payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentSummary(
    Long id,
    String initiatorUsername,
    String receiverUsername,
    String currencySymbol,
    BigDecimal amount,
    Instant transactionTime
) {

}
