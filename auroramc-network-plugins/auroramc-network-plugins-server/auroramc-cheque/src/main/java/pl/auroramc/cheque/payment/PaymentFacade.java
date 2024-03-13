package pl.auroramc.cheque.payment;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public interface PaymentFacade {

  static PaymentFacade getPaymentFacade(final Logger logger, final Juliet juliet) {
    final SqlPaymentRepository sqlPaymentRepository = new SqlPaymentRepository(juliet);
    sqlPaymentRepository.createPaymentSchemaIfRequired();
    return new PaymentService(logger, sqlPaymentRepository);
  }

  CompletableFuture<Void> createPayment(final Payment payment);
}
