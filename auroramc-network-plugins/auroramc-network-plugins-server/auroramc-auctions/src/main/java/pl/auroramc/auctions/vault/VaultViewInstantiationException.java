package pl.auroramc.auctions.vault;

class VaultViewInstantiationException extends IllegalStateException {

  VaultViewInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  VaultViewInstantiationException(final String message) {
    super(message);
  }
}
