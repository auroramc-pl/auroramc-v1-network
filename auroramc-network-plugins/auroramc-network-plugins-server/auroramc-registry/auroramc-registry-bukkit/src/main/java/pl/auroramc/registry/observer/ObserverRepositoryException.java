package pl.auroramc.registry.observer;

class ObserverRepositoryException extends IllegalStateException {

  ObserverRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }

  ObserverRepositoryException(final String message) {
    super(message);
  }
}
