package pl.auroramc.auctions.vault;

class VaultRepositoryException extends IllegalStateException {

  VaultRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
