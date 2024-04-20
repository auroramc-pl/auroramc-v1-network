package pl.auroramc.cheque.payment;

import static pl.auroramc.commons.CompletableFutureUtils.NIL;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.scheduler.Scheduler;

class PaymentService implements PaymentFacade {

  private final Scheduler scheduler;
  private final BigDecimal paymentBuffer;
  private final PaymentRepository paymentRepository;

  PaymentService(
      final Scheduler scheduler,
      final BigDecimal paymentBuffer,
      final PaymentRepository paymentRepository) {
    this.scheduler = scheduler;
    this.paymentBuffer = paymentBuffer;
    this.paymentRepository = paymentRepository;
  }

  @Override
  public CompletableFuture<Void> createPayment(final Payment payment) {
    if (payment.amount().compareTo(paymentBuffer) < 0) {
      return NIL;
    }

    return scheduler.run(ASYNC, () -> paymentRepository.createPayment(payment));
  }
}
