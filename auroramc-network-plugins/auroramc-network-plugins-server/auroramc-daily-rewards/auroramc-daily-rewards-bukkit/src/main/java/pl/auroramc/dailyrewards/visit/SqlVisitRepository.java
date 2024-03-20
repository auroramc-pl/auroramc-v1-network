package pl.auroramc.dailyrewards.visit;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.dailyrewards.visit.SqlVisitRepositoryQuery.CREATE_VISIT;
import static pl.auroramc.dailyrewards.visit.SqlVisitRepositoryQuery.CREATE_VISIT_SCHEMA;
import static pl.auroramc.dailyrewards.visit.SqlVisitRepositoryQuery.FIND_VISITS_BY_USER_ID;
import static pl.auroramc.dailyrewards.visit.SqlVisitRepositoryQuery.FIND_VISITS_BY_USER_ID_BETWEEN;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import moe.rafal.juliet.Juliet;

class SqlVisitRepository implements VisitRepository {

  private final Juliet juliet;

  SqlVisitRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createVisitSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_VISIT_SCHEMA);
    } catch (final SQLException exception) {
      throw new VisitRepositoryException(
          "Could not create visit schema, because of unexpected exception.", exception);
    }
  }

  @Override
  public void createVisit(final Visit visit) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_VISIT)) {
      statement.setLong(1, visit.getUserId().intValue());
      statement.setLong(2, visit.getSessionDuration().toSeconds());
      statement.setTimestamp(3, Timestamp.from(visit.getSessionStartTime()));
      statement.setTimestamp(4, Timestamp.from(visit.getSessionDitchTime()));
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new VisitRepositoryException(
          "Could not create visit, because of unexpected exception, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public Set<Visit> findVisitsByUserId(final Long userId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_VISITS_BY_USER_ID)) {
      statement.setLong(1, userId);

      final Set<Visit> results = new HashSet<>();
      final ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(mapResultSetToVisit(resultSet));
      }

      return results;
    } catch (final SQLException exception) {
      throw new VisitRepositoryException(
          "Could not find visits by user id, because of unexpected exception.", exception);
    }
  }

  @Override
  public Set<Visit> findVisitsByUserIdBetween(
      final Long userId, final Instant from, final Instant to) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(FIND_VISITS_BY_USER_ID_BETWEEN)) {
      statement.setLong(1, userId);
      statement.setTimestamp(2, Timestamp.from(from));
      statement.setTimestamp(3, Timestamp.from(to));

      final Set<Visit> results = new HashSet<>();
      final ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(mapResultSetToVisit(resultSet));
      }

      return results;
    } catch (final SQLException exception) {
      throw new VisitRepositoryException(
          "Could not find visits by user id, because of unexpected exception.", exception);
    }
  }

  private Visit mapResultSetToVisit(final ResultSet resultSet) throws SQLException {
    return new Visit(
        resultSet.getLong("user_id"),
        ofSeconds(resultSet.getLong("session_duration")),
        resultSet.getTimestamp("session_start_time").toInstant(),
        resultSet.getTimestamp("session_ditch_time").toInstant());
  }
}
