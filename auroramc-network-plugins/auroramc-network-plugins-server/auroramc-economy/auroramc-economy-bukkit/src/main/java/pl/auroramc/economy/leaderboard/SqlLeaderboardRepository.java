package pl.auroramc.economy.leaderboard;

import static pl.auroramc.economy.leaderboard.SqlLeaderboardRepositoryQuery.GET_LEADERBOARD_ENTRIES_BY_BALANCE_ASCENDING;
import static pl.auroramc.economy.leaderboard.SqlLeaderboardRepositoryQuery.GET_LEADERBOARD_ENTRY_BY_UNIQUE_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

class SqlLeaderboardRepository implements LeaderboardRepository {

  private final Juliet juliet;

  SqlLeaderboardRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  @Override
  public LeaderboardEntry getLeaderboardEntryByUniqueId(
      final Long currencyId, final UUID uniqueId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(GET_LEADERBOARD_ENTRY_BY_UNIQUE_ID)) {
      statement.setLong(1, currencyId);
      statement.setLong(2, currencyId);
      statement.setObject(3, uniqueId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToLeaderboardEntry(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new LeaderboardRepositoryException(
          "Could not find leaderboard entry identified by %s, because of unexpected exception"
              .formatted(uniqueId),
          exception);
    }
  }

  @Override
  public List<LeaderboardEntry> getLeaderboardEntriesByBalanceAscending(final Long currencyId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(GET_LEADERBOARD_ENTRIES_BY_BALANCE_ASCENDING)) {
      statement.setLong(1, currencyId);

      final List<LeaderboardEntry> results = new LinkedList<>();
      final ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(mapResultSetToLeaderboardEntry(resultSet));
      }

      return results;
    } catch (final SQLException exception) {
      throw new LeaderboardRepositoryException(
          "Could not find leaderboard entries for currency identified by %s, because of unexpected exception"
              .formatted(currencyId),
          exception);
    }
  }

  private LeaderboardEntry mapResultSetToLeaderboardEntry(final ResultSet resultSet)
      throws SQLException {
    return new LeaderboardEntry(
        UUID.fromString(resultSet.getString("unique_id")),
        resultSet.getString("username"),
        resultSet.getLong("position"),
        resultSet.getLong("currency_id"),
        resultSet.getBigDecimal("balance"));
  }
}
