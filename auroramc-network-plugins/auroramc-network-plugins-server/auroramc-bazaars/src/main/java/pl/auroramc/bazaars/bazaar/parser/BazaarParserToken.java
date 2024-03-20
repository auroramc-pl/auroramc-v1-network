package pl.auroramc.bazaars.bazaar.parser;

public enum BazaarParserToken {
  MERCHANT(0),
  QUANTITY(1),
  MATERIAL(3),
  PRICE(2);

  private final int lineIndex;

  BazaarParserToken(int lineIndex) {
    this.lineIndex = lineIndex;
  }

  public int getLineIndex() {
    return lineIndex;
  }
}
