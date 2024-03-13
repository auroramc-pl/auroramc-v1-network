package pl.auroramc.economy.account;

class AccountRepositoryException extends IllegalStateException {

  AccountRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  AccountRepositoryException(final String message) {
    super(message);
  }
}
