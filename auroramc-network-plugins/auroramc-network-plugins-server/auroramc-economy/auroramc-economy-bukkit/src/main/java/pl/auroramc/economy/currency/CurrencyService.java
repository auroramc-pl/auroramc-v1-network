package pl.auroramc.economy.currency;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class CurrencyService implements CurrencyFacade {

  private final Scheduler scheduler;
  private final CurrencyRepository currencyRepository;
  private final LoadingCache<Long, Currency> currencyCache;

  CurrencyService(final Scheduler scheduler, final CurrencyRepository currencyRepository) {
    this.scheduler = scheduler;
    this.currencyRepository = currencyRepository;
    this.currencyCache =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(90))
            .build(currencyRepository::findCurrencyById);
  }

  @Override
  public Currency getCurrencyById(final Long currencyId) {
    return currencyCache.get(currencyId);
  }

  @Override
  public void createCurrency(final Currency currency) {
    scheduler
        .run(SYNC, () -> currencyRepository.createCurrency(currency))
        .thenAccept(state -> currencyCache.put(currency.getId(), currency));
  }

  @Override
  public void deleteCurrency(final Currency currency) {
    scheduler
        .run(SYNC, () -> currencyRepository.deleteCurrency(currency))
        .thenAccept(state -> currencyCache.invalidate(currency.getId()));
  }
}
