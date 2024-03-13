package pl.auroramc.bazaars.bazaar.parser;

import pl.auroramc.bazaars.sign.SignDelegate;

public interface BazaarParser {

  static BazaarParser getBazaarParser() {
    return new BazaarParserImpl();
  }

  BazaarParsingContext parseContext(final SignDelegate sign);

  BazaarParsingContext parseContextOrNull(final SignDelegate sign);
}
