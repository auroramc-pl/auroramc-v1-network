package pl.auroramc.auth.mail;

class MailDeliveryException extends IllegalStateException {

  MailDeliveryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  MailDeliveryException(final String message) {
    super(message);
  }
}
