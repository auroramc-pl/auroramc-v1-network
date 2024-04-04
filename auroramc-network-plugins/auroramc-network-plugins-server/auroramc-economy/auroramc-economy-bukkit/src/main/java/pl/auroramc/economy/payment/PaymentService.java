package pl.auroramc.economy.payment;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.scheduler.Scheduler;

class PaymentService implements PaymentFacade {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private final Scheduler scheduler;
  private final PaymentConfig paymentConfig;
  private final PaymentRepository paymentRepository;

  PaymentService(
      final Scheduler scheduler,
      final PaymentConfig paymentConfig,
      final PaymentRepository paymentRepository) {
    this.scheduler = scheduler;
    this.paymentConfig = paymentConfig;
    this.paymentRepository = paymentRepository;
  }

  @Override
  public CompletableFuture<Payment> getPaymentById(final Long paymentId) {
    return scheduler.supply(ASYNC, () -> paymentRepository.findPaymentById(paymentId));
  }

  @Override
  public CompletableFuture<List<PaymentSummary>> getPaymentSummariesByInitiatorId(
      final Long initiatorId) {
    return scheduler.supply(
        ASYNC, () -> paymentRepository.findPaymentSummariesByInitiatorId(initiatorId));
  }

  @Override
  public CompletableFuture<List<PaymentSummary>> getPaymentSummariesByReceiverId(
      final Long receiverId) {
    return scheduler.supply(
        ASYNC, () -> paymentRepository.findPaymentSummariesByReceiverId(receiverId));
  }

  @Override
  public CompletableFuture<Void> createPayment(final Payment payment) {
    return payment.getAmount().compareTo(paymentConfig.paymentBuffer) >= 0
        ? scheduler.run(ASYNC, () -> paymentRepository.createPayment(payment))
        : EMPTY_FUTURE;
  }

  @Override
  public CompletableFuture<Void> deletePayment(final Payment payment) {
    return scheduler.run(ASYNC, () -> paymentRepository.deletePayment(payment));
  }
}
