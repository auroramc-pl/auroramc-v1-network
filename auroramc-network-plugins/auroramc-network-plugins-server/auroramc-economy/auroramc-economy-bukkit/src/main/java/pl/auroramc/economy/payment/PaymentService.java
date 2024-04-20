package pl.auroramc.economy.payment;

import static pl.auroramc.commons.CompletableFutureUtils.NIL;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.commons.scheduler.Scheduler;

class PaymentService implements PaymentFacade {

  private final Logger logger;
  private final Scheduler scheduler;
  private final PaymentConfig paymentConfig;
  private final PaymentRepository paymentRepository;

  PaymentService(
      final Logger logger,
      final Scheduler scheduler,
      final PaymentConfig paymentConfig,
      final PaymentRepository paymentRepository) {
    this.logger = logger;
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
    if (payment.getAmount().compareTo(paymentConfig.paymentBuffer) < 0) {
      logger.fine("Payment amount is below the buffer, skipping payment creation");
      return NIL;
    }

    return scheduler.run(ASYNC, () -> paymentRepository.createPayment(payment));
  }

  @Override
  public CompletableFuture<Void> deletePayment(final Payment payment) {
    return scheduler.run(ASYNC, () -> paymentRepository.deletePayment(payment));
  }
}
