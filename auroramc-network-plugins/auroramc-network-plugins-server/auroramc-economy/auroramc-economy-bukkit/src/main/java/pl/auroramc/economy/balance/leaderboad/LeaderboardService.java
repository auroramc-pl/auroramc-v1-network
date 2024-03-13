package pl.auroramc.economy.balance.leaderboad;

import static java.time.Duration.ofSeconds;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.Optional;
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
        .build(key -> leaderboardRepository
            .getLeaderboardEntryByUniqueId(key.currencyId(), key.uniqueId())
            .orElse(null));
  }

  @Override
  public List<LeaderboardEntry> getLeaderboardEntriesByCurrencyId(
      final Long currencyId
  ) {
    return entryToCurrencyIdCache.get(currencyId);
  }

  @Override
  public Optional<LeaderboardEntry> getLeaderboardEntryByCurrencyIdAndUniqueId(
      final Long currencyId, final UUID uniqueId
  ) {
    final CurrencyIdToUniqueIdCompositeKey compositeKey = new CurrencyIdToUniqueIdCompositeKey(currencyId, uniqueId);
    return Optional.ofNullable(entryToCurrencyIdAndUniqueIdCache.get(compositeKey));
  }

  private record CurrencyIdToUniqueIdCompositeKey(Long currencyId, UUID uniqueId) {

  }
}
