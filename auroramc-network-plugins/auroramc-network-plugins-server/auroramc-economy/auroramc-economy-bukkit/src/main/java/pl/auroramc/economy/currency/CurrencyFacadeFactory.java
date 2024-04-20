package pl.auroramc.economy.currency;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class CurrencyFacadeFactory {

  private CurrencyFacadeFactory() {}

  public static CurrencyFacade getCurrencyFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlCurrencyRepository sqlCurrencyRepository = new SqlCurrencyRepository(juliet);
    sqlCurrencyRepository.createCurrencySchemaIfRequired();
    return new CurrencyService(scheduler, sqlCurrencyRepository);
  }
}
