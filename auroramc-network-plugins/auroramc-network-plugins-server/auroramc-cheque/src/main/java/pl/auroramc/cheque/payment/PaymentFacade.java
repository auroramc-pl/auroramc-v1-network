package pl.auroramc.cheque.payment;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public interface PaymentFacade {

  static PaymentFacade getPaymentFacade(
      final Scheduler scheduler, final BigDecimal paymentBuffer, final Juliet juliet) {
    final SqlPaymentRepository sqlPaymentRepository = new SqlPaymentRepository(juliet);
    sqlPaymentRepository.createPaymentSchemaIfRequired();
    return new PaymentService(scheduler, paymentBuffer, sqlPaymentRepository);
  }

  CompletableFuture<Void> createPayment(final Payment payment);
}
