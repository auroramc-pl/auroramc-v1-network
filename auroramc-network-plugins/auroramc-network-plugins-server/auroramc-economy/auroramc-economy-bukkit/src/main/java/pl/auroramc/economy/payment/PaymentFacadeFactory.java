package pl.auroramc.economy.payment;

import java.math.BigDecimal;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class PaymentFacadeFactory {

  private PaymentFacadeFactory() {

  }

  public static PaymentFacade producePaymentFacade(
      final Logger logger,
      final Juliet juliet,
      final BigDecimal paymentBuffer
  ) {
    final SqlPaymentRepository sqlPaymentRepository = new SqlPaymentRepository(juliet);
    sqlPaymentRepository.createPaymentSchemaIfRequired();
    return new PaymentService(logger, sqlPaymentRepository, paymentBuffer);
  }
}
