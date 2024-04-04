package pl.auroramc.economy.payment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public enum PaymentDirection {
  INCOMING(PaymentFacade::getPaymentSummariesByReceiverId),
  OUTGOING(PaymentFacade::getPaymentSummariesByInitiatorId);

  private final BiFunction<PaymentFacade, Long, CompletableFuture<List<PaymentSummary>>>
      paymentSummariesRetriever;

  PaymentDirection(
      final BiFunction<PaymentFacade, Long, CompletableFuture<List<PaymentSummary>>>
          paymentSummariesRetriever) {
    this.paymentSummariesRetriever = paymentSummariesRetriever;
  }

  CompletableFuture<List<PaymentSummary>> getPaymentSummaries(
      final PaymentFacade paymentFacade, final Long userId) {
    return this.paymentSummariesRetriever.apply(paymentFacade, userId);
  }
}
