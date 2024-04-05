package pl.auroramc.economy.payment;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class PaymentFacadeFactory {

  private PaymentFacadeFactory() {}

  public static PaymentFacade getPaymentFacade(
      final Scheduler scheduler,
      final Logger logger,
      final Juliet juliet,
      final PaymentConfig paymentConfig) {
    final SqlPaymentRepository sqlPaymentRepository = new SqlPaymentRepository(juliet);
    sqlPaymentRepository.createPaymentSchemaIfRequired();
    return new PaymentService(logger, scheduler, paymentConfig, sqlPaymentRepository);
  }
}
