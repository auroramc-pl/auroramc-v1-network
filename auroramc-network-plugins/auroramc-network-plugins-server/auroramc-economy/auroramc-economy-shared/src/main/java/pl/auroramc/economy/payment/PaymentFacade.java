package pl.auroramc.economy.payment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PaymentFacade {

  CompletableFuture<Payment> getPaymentById(final Long paymentId);

  CompletableFuture<List<PaymentSummary>> getPaymentSummariesByInitiatorId(final Long initiatorId);

  CompletableFuture<List<PaymentSummary>> getPaymentSummariesByReceiverId(final Long receiverId);

  CompletableFuture<Void> createPayment(final Payment payment);

  CompletableFuture<Void> deletePayment(final Payment payment);
}
