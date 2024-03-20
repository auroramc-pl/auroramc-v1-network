package pl.auroramc.bazaars.bazaar.parser;

final class BazaarParsingAssertions {

  private BazaarParsingAssertions() {}

  static <T> T assertNotNull(final T value, final String errorMessage) {
    if (value == null) {
      throw new BazaarParsingException(errorMessage);
    }

    return value;
  }

  static <T extends CharSequence> T assertNotEmpty(final T value, final String errorMessage) {
    if (value == null || value.isEmpty()) {
      throw new BazaarParsingException(errorMessage);
    }

    return value;
  }
}
