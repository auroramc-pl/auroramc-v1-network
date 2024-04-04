package pl.auroramc.economy.economy;

class EconomyException extends IllegalStateException {

  EconomyException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
