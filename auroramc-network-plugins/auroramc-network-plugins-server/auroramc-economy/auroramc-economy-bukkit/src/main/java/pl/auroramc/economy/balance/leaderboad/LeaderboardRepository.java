package pl.auroramc.economy.balance.leaderboad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface LeaderboardRepository {

  List<LeaderboardEntry> getLeaderboardEntriesByBalanceAscending(
      final Long currencyId
  );

  Optional<LeaderboardEntry> getLeaderboardEntryByUniqueId(
      final Long currencyId, final UUID uniqueId
  );
}
