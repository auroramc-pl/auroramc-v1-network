package pl.auroramc.bounties.progress;

class BountyProgressRepositoryException extends IllegalStateException {

  BountyProgressRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  BountyProgressRepositoryException(final String message) {
    super(message);
  }
}
