package pl.auroramc.cheque.payment;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class PaymentService implements PaymentFacade {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private static final BigDecimal PAYMENT_AMOUNT_BUFFER = BigDecimal.valueOf(1_000);
  private final Logger logger;
  private final PaymentRepository paymentRepository;

  PaymentService(final Logger logger, final PaymentRepository paymentRepository) {
    this.logger = logger;
    this.paymentRepository = paymentRepository;
  }

  @Override
  public CompletableFuture<Void> createPayment(final Payment payment) {
    return payment.amount().compareTo(PAYMENT_AMOUNT_BUFFER) >= 0
        ? runAsync(() -> paymentRepository.createPayment(payment)).exceptionally(exception -> delegateCaughtException(logger, exception))
        : EMPTY_FUTURE;
  }
}
