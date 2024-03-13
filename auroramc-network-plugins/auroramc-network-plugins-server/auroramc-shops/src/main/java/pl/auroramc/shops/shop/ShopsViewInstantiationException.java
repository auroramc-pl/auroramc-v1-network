package pl.auroramc.shops.shop;

class ShopsViewInstantiationException extends IllegalStateException {

  ShopsViewInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  ShopsViewInstantiationException(final String message) {
    super(message);
  }
}
