package pl.auroramc.economy.balance.leaderboard;

import static java.time.Duration.ofSeconds;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.UUID;

class LeaderboardService implements LeaderboardFacade {

  private final LoadingCache<Long, List<LeaderboardEntry>> entryToCurrencyIdCache;
  private final LoadingCache<CurrencyIdToUniqueIdCompositeKey, LeaderboardEntry> entryToCurrencyIdAndUniqueIdCache;

  LeaderboardService(final LeaderboardRepository leaderboardRepository) {
    this.entryToCurrencyIdCache = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(30))
        .build(leaderboardRepository::getLeaderboardEntriesByBalanceAscending);
    this.entryToCurrencyIdAndUniqueIdCache = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(90))
        .build(key ->
            leaderboardRepository.getLeaderboardEntryByUniqueId(
                key.currencyId(),
                key.uniqueId()
            )
        );
  }

  @Override
  public List<LeaderboardEntry> getLeaderboardEntriesByCurrencyId(
      final Long currencyId
  ) {
    return entryToCurrencyIdCache.get(currencyId);
  }

  @Override
  public LeaderboardEntry getLeaderboardEntryByCurrencyIdAndUniqueId(
      final Long currencyId, final UUID uniqueId
  ) {
    return entryToCurrencyIdAndUniqueIdCache.get(
        new CurrencyIdToUniqueIdCompositeKey(currencyId, uniqueId)
    );
  }

  private record CurrencyIdToUniqueIdCompositeKey(Long currencyId, UUID uniqueId) {

  }
}
