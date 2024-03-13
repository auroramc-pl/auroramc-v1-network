package pl.auroramc.punishments.punishment;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.punishments.punishment.SqlPunishmentRepositoryQuery.CREATE_PUNISHMENT;
import static pl.auroramc.punishments.punishment.SqlPunishmentRepositoryQuery.CREATE_PUNISHMENT_SCHEMA_IF_REQUIRED;
import static pl.auroramc.punishments.punishment.SqlPunishmentRepositoryQuery.FIND_PUNISHMENTS_BY_PENALIZED_ID;
import static pl.auroramc.punishments.punishment.SqlPunishmentRepositoryQuery.FIND_PUNISHMENT_BY_ID;
import static pl.auroramc.punishments.punishment.SqlPunishmentRepositoryQuery.FIND_PUNISHMENT_BY_PENALIZED_ID_WITH_SCOPE_AND_STATE;
import static pl.auroramc.punishments.punishment.SqlPunishmentRepositoryQuery.UPDATE_PUNISHMENT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import moe.rafal.juliet.Juliet;

class SqlPunishmentRepository implements PunishmentRepository {

  private final Juliet juliet;

  SqlPunishmentRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createPunishmentSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_PUNISHMENT_SCHEMA_IF_REQUIRED);
    } catch (final SQLException exception) {
      throw new PunishmentRepositoryException(
          "Could not create punishment schema, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public Punishment findPunishmentById(final Long id) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_PUNISHMENT_BY_ID)
    ) {
      statement.setLong(1, id);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToPunishment(resultSet);
        }
        return null;
      }
    } catch (final SQLException exception) {
      throw new PunishmentRepositoryException(
          "Could not find punishment by id, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public Punishment findPunishmentByPenalizedIdWithScopeAndState(
      final Long penalizedId,
      final PunishmentScope scope,
      final PunishmentState state
  ) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_PUNISHMENT_BY_PENALIZED_ID_WITH_SCOPE_AND_STATE)
    ) {
      statement.setLong(1, penalizedId);
      statement.setString(2, scope.name());
      statement.setString(3, state.name());
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToPunishment(resultSet);
        }
        return null;
      }
    } catch (final SQLException exception) {
      throw new PunishmentRepositoryException(
          "Could not find punishment by penalized id with scope and state, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public List<Punishment> findPunishmentsByPenalizedId(
      final Long penalizedId
  ) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_PUNISHMENTS_BY_PENALIZED_ID)
    ) {
      statement.setLong(1, penalizedId);

      final List<Punishment> results = new ArrayList<>();
      try (final ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          results.add(mapResultSetToPunishment(resultSet));
        }
        return results;
      }
    } catch (final SQLException exception) {
      throw new PunishmentRepositoryException(
          "Could not find punishments by penalized id, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void createPunishment(final Punishment punishment) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_PUNISHMENT, RETURN_GENERATED_KEYS)
    ) {
      setPunishmentParameters(statement, punishment);
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new PunishmentRepositoryException(
          "Could not create punishment, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void updatePunishment(final Punishment punishment) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_PUNISHMENT)
    ) {
      setPunishmentParameters(statement, punishment, true);
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new PunishmentRepositoryException(
          "Could not create punishment, because of unexpected exception.",
          exception
      );
    }
  }

  private void setPunishmentParameters(
      final PreparedStatement statement,
      final Punishment punishment
  ) throws SQLException {
    setPunishmentParameters(statement, punishment, false);
  }

  private void setPunishmentParameters(
      final PreparedStatement statement,
      final Punishment punishment,
      final boolean whetherRequiresId
  ) throws SQLException {
    statement.setLong(1, punishment.getPenalizedId());
    statement.setLong(2, punishment.getPerformerId());
    statement.setString(3, punishment.getReason());
    statement.setLong(4, punishment.getPeriod().getSeconds());
    statement.setString(5, punishment.getScope().name());
    statement.setString(6, punishment.getState().name());
    statement.setTimestamp(7, Timestamp.from(punishment.getIssuedAt()));
    statement.setTimestamp(8, Timestamp.from(punishment.getExpiresAt()));
    if (whetherRequiresId) {
      statement.setLong(9, punishment.getId());
    }
  }

  private Punishment mapResultSetToPunishment(final ResultSet resultSet) throws SQLException {
    return new Punishment(
        resultSet.getLong("id"),
        resultSet.getLong("penalized_id"),
        resultSet.getLong("performer_id"),
        resultSet.getString("reason"),
        Duration.ofSeconds(resultSet.getLong("period")),
        PunishmentScope.valueOf(resultSet.getString("scope")),
        PunishmentState.valueOf(resultSet.getString("state")),
        resultSet.getTimestamp("issued_at").toInstant(),
        resultSet.getTimestamp("expires_at").toInstant()
    );
  }
}
