package pl.auroramc.shops;

class ShopsInstantiationException extends IllegalStateException {

  ShopsInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  ShopsInstantiationException(final String message) {
    super(message);
  }
}
