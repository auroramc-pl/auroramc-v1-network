package pl.auroramc.economy.currency;

import java.util.Objects;

public class Currency {

  private Long id;
  private String name;
  private String symbol;
  private String description;

  Currency(final Long id, final String name, final String symbol, final String description) {
    this.id = id;
    this.name = name;
    this.symbol = symbol;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(final String symbol) {
    this.symbol = symbol;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Override
  public boolean equals(final Object comparedObject) {
    if (this == comparedObject) {
      return true;
    }

    if (comparedObject == null || getClass() != comparedObject.getClass()) {
      return false;
    }

    final Currency currency = (Currency) comparedObject;
    return Objects.equals(id, currency.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
