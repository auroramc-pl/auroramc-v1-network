package pl.auroramc.economy.leaderboard;

import static java.time.Duration.ofSeconds;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.UUID;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class LeaderboardService implements LeaderboardFacade {

  private final LoadingCache<Long, List<LeaderboardEntry>> entryToCurrencyIdCache;
  private final LoadingCache<CurrencyIdToUniqueIdCompositeKey, LeaderboardEntry>
      entryToCurrencyIdAndUniqueIdCache;

  LeaderboardService(final Scheduler scheduler, final LeaderboardRepository leaderboardRepository) {
    this.entryToCurrencyIdCache =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .build(leaderboardRepository::getLeaderboardEntriesByBalanceAscending);
    this.entryToCurrencyIdAndUniqueIdCache =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(90))
            .build(
                key ->
                    leaderboardRepository.getLeaderboardEntryByUniqueId(
                        key.currencyId(), key.uniqueId()));
  }

  @Override
  public List<LeaderboardEntry> getLeaderboardEntriesByCurrencyId(final Long currencyId) {
    return entryToCurrencyIdCache.get(currencyId);
  }

  @Override
  public LeaderboardEntry getLeaderboardEntryByCurrencyIdAndUniqueId(
      final Long currencyId, final UUID uniqueId) {
    return entryToCurrencyIdAndUniqueIdCache.get(
        new CurrencyIdToUniqueIdCompositeKey(currencyId, uniqueId));
  }

  private record CurrencyIdToUniqueIdCompositeKey(Long currencyId, UUID uniqueId) {}
}
