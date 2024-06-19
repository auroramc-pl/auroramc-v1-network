package pl.auroramc.spawners.spawner;

class SpawnersViewInstantiationException extends IllegalStateException {

  SpawnersViewInstantiationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  SpawnersViewInstantiationException(final String message) {
    super(message);
  }
}
