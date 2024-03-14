package pl.auroramc.auth.user;

class UserRedirectingException extends IllegalArgumentException {

  UserRedirectingException(final String message) {
    super(message);
  }
}
