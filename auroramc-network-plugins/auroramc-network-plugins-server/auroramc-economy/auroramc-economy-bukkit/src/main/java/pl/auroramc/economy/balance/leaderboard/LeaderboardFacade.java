package pl.auroramc.economy.balance.leaderboard;

import java.util.List;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

public interface LeaderboardFacade {

  static LeaderboardFacade getLeaderboardFacade(final Juliet juliet) {
    return new LeaderboardService(new SqlLeaderboardRepository(juliet));
  }

  List<LeaderboardEntry> getLeaderboardEntriesByCurrencyId(
      final Long currencyId
  );

  LeaderboardEntry getLeaderboardEntryByCurrencyIdAndUniqueId(
      final Long currencyId, final UUID uniqueId
  );
}
