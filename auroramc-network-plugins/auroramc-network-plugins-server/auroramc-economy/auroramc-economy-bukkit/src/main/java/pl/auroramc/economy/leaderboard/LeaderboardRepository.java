package pl.auroramc.economy.leaderboard;

import java.util.List;
import java.util.UUID;

interface LeaderboardRepository {

  LeaderboardEntry getLeaderboardEntryByUniqueId(final Long currencyId, final UUID uniqueId);

  List<LeaderboardEntry> getLeaderboardEntriesByBalanceAscending(final Long currencyId);
}
