package pl.auroramc.economy.payment;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class PaymentService implements PaymentFacade {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private final Logger logger;
  private final PaymentRepository paymentRepository;
  private final BigDecimal paymentAmountBuffer;

  PaymentService(
      final Logger logger,
      final PaymentRepository paymentRepository,
      final BigDecimal paymentAmountBuffer) {
    this.logger = logger;
    this.paymentRepository = paymentRepository;
    this.paymentAmountBuffer = paymentAmountBuffer;
  }

  @Override
  public CompletableFuture<Payment> getPaymentById(final Long paymentId) {
    return supplyAsync(() -> paymentRepository.findPaymentById(paymentId))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<List<PaymentSummary>> getPaymentSummariesByInitiatorId(
      final Long initiatorId) {
    return supplyAsync(() -> paymentRepository.findPaymentSummariesByInitiatorId(initiatorId))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<List<PaymentSummary>> getPaymentSummariesByReceiverId(
      final Long receiverId) {
    return supplyAsync(() -> paymentRepository.findPaymentSummariesByReceiverId(receiverId))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> createPayment(final Payment payment) {
    return payment.getAmount().compareTo(paymentAmountBuffer) >= 0
        ? runAsync(() -> paymentRepository.createPayment(payment))
            .exceptionally(exception -> delegateCaughtException(logger, exception))
        : EMPTY_FUTURE;
  }

  @Override
  public CompletableFuture<Void> deletePayment(final Payment payment) {
    return runAsync(() -> paymentRepository.deletePayment(payment))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
