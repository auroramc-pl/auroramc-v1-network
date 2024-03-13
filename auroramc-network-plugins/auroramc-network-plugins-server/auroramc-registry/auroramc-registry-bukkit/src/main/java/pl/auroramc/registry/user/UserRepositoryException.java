package pl.auroramc.registry.user;

class UserRepositoryException extends IllegalStateException {

  UserRepositoryException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
