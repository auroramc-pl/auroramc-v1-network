package pl.auroramc.economy.currency;

public final class CurrencyBuilder {

  private String name;
  private String symbol;
  private String description;

  private CurrencyBuilder() {}

  public static CurrencyBuilder newBuilder() {
    return new CurrencyBuilder();
  }

  public CurrencyBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public CurrencyBuilder withSymbol(final String symbol) {
    this.symbol = symbol;
    return this;
  }

  public CurrencyBuilder withDescription(final String description) {
    this.description = description;
    return this;
  }

  public Currency build() {
    return new Currency(null, name, symbol, description);
  }
}
