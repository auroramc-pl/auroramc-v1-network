package pl.auroramc.spawners;

class SpawnersInstantiationException extends IllegalStateException {

  SpawnersInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  SpawnersInstantiationException(final String message) {
    super(message);
  }
}
