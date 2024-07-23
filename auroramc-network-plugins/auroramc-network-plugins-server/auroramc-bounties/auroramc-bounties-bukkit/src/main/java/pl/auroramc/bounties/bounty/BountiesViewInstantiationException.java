package pl.auroramc.bounties.bounty;

class BountiesViewInstantiationException extends IllegalStateException {

  BountiesViewInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  BountiesViewInstantiationException(final String message) {
    super(message);
  }
}
