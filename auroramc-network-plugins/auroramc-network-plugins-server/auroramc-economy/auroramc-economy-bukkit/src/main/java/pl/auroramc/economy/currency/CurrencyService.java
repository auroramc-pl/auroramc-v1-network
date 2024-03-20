package pl.auroramc.economy.currency;

import static java.time.Duration.ofSeconds;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

class CurrencyService implements CurrencyFacade {

  private final CurrencyRepository currencyRepository;
  private final LoadingCache<Long, Currency> currencyCache;

  CurrencyService(final CurrencyRepository currencyRepository) {
    this.currencyRepository = currencyRepository;
    this.currencyCache =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(90))
            .build(currencyRepository::findCurrencyById);
  }

  @Override
  public Currency getCurrencyById(final Long currencyId) {
    return currencyCache.get(currencyId);
  }

  @Override
  public void createCurrency(final Currency currency) {
    currencyRepository.createCurrency(currency);
    currencyCache.put(currency.getId(), currency);
  }

  @Override
  public void deleteCurrency(final Currency currency) {
    currencyRepository.deleteCurrency(currency);
    currencyCache.invalidate(currency.getId());
  }
}
