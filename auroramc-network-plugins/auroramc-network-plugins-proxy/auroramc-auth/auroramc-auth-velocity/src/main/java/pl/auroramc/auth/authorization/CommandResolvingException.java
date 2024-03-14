package pl.auroramc.auth.authorization;

class CommandResolvingException extends IllegalStateException {

  CommandResolvingException(final String message) {
    super(message);
  }
}
