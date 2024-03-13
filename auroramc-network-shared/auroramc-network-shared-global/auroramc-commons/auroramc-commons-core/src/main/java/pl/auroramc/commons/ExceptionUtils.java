package pl.auroramc.commons;

import static java.util.logging.Level.SEVERE;

import java.util.logging.Logger;

public final class ExceptionUtils {

  private static final String EXCEPTION_CAUGHT_MESSAGE =
      "Caught an exception in future execution: %s";

  private ExceptionUtils() {

  }

  public static <T> T delegateCaughtException(final Logger logger, final Throwable cause) {
    logger.log(SEVERE, EXCEPTION_CAUGHT_MESSAGE.formatted(cause.getMessage()), cause);
    return null;
  }
}
