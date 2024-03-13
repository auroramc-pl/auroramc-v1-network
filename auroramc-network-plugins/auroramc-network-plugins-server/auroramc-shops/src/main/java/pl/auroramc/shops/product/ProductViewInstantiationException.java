package pl.auroramc.shops.product;

class ProductViewInstantiationException extends IllegalArgumentException {

  ProductViewInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  ProductViewInstantiationException(final String message) {
    super(message);
  }
}
