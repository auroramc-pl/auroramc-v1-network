package pl.auroramc.economy.leaderboard;

import java.util.List;
import java.util.UUID;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public interface LeaderboardFacade {

  static LeaderboardFacade getLeaderboardFacade(final Scheduler scheduler, Juliet juliet) {
    return new LeaderboardService(scheduler, new SqlLeaderboardRepository(juliet));
  }

  List<LeaderboardEntry> getLeaderboardEntriesByCurrencyId(final Long currencyId);

  LeaderboardEntry getLeaderboardEntryByCurrencyIdAndUniqueId(
      final Long currencyId, final UUID uniqueId);
}
