package pl.auroramc.economy.currency;

public interface CurrencyFacade {

  Currency getCurrencyById(final Long currencyId);

  void createCurrency(final Currency currency);

  void deleteCurrency(final Currency currency);
}
