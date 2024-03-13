package pl.auroramc.economy.balance.leaderboad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

public interface LeaderboardFacade {

  static LeaderboardFacade getLeaderboardFacade(final Juliet juliet) {
    return new LeaderboardService(new SqlLeaderboardRepository(juliet));
  }

  List<LeaderboardEntry> getLeaderboardEntriesByCurrencyId(
      final Long currencyId
  );

  Optional<LeaderboardEntry> getLeaderboardEntryByCurrencyIdAndUniqueId(
      final Long currencyId, final UUID uniqueId
  );
}
