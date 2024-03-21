package pl.auroramc.economy.currency;

import moe.rafal.juliet.Juliet;

public final class CurrencyFacadeFactory {

  private CurrencyFacadeFactory() {}

  public static CurrencyFacade getCurrencyFacade(final Juliet juliet) {
    final SqlCurrencyRepository sqlCurrencyRepository = new SqlCurrencyRepository(juliet);
    sqlCurrencyRepository.createCurrencySchemaIfRequired();
    return new CurrencyService(sqlCurrencyRepository);
  }
}
