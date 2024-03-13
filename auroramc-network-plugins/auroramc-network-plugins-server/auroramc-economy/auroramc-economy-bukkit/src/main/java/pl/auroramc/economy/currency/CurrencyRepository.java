package pl.auroramc.economy.currency;

interface CurrencyRepository {

  Currency findCurrencyById(final Long currencyId);

  void createCurrency(final Currency currency);

  void deleteCurrency(final Currency currency);
}
