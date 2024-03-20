package pl.auroramc.cheque.payment;

class PaymentRepositoryException extends IllegalStateException {

  PaymentRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
