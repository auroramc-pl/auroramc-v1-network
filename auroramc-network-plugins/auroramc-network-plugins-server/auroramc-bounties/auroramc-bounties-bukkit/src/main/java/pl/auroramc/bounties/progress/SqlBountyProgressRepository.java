package pl.auroramc.bounties.progress;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.bounties.progress.SqlBountyProgressRepositoryQuery.CREATE_BOUNTY_PROGRESS;
import static pl.auroramc.bounties.progress.SqlBountyProgressRepositoryQuery.CREATE_BOUNTY_PROGRESS_SCHEMA;
import static pl.auroramc.bounties.progress.SqlBountyProgressRepositoryQuery.FIND_BOUNTY_PROGRESS_BY_USER_ID;
import static pl.auroramc.bounties.progress.SqlBountyProgressRepositoryQuery.UPDATE_BOUNTY_PROGRESS;
import static pl.auroramc.commons.sql.repository.SqlRepositoryUtils.getLocalDate;
import static pl.auroramc.commons.sql.repository.SqlRepositoryUtils.setLocalDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlBountyProgressRepository implements BountyProgressRepository {

  private final Juliet juliet;

  SqlBountyProgressRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createBountyProgressSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_BOUNTY_PROGRESS_SCHEMA);
    } catch (final SQLException exception) {
      throw new BountyProgressRepositoryException(
          "Could not create schema for bounty progress entity, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public BountyProgress findBountyProgressByUserId(final Long userId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(FIND_BOUNTY_PROGRESS_BY_USER_ID)) {
      statement.setLong(1, userId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToBountyProgress(resultSet);
        }
      }
    } catch (final SQLException exception) {
      throw new BountyProgressRepositoryException(
          "Could not find bounty progress for user %d, because of unexpected exception."
              .formatted(userId),
          exception);
    }
    return null;
  }

  @Override
  public void createBountyProgress(final BountyProgress bountyProgress) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_BOUNTY_PROGRESS, RETURN_GENERATED_KEYS)) {
      statement.setLong(1, bountyProgress.getUserId());
      statement.setLong(2, bountyProgress.getDay());
      setLocalDate(statement, 3, bountyProgress.getAcquisitionDate());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          bountyProgress.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new BountyProgressRepositoryException(
          "Could not create bounty progress for user %d, because of unexpected exception."
              .formatted(bountyProgress.getUserId()),
          exception);
    }
  }

  @Override
  public void updateBountyProgress(final BountyProgress bountyProgress) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_BOUNTY_PROGRESS)) {
      statement.setLong(1, bountyProgress.getDay());
      setLocalDate(statement, 2, bountyProgress.getAcquisitionDate());
      statement.setLong(3, bountyProgress.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new BountyProgressRepositoryException(
          "Could not update bounty progress for user %d, because of unexpected exception."
              .formatted(bountyProgress.getUserId()),
          exception);
    }
  }

  private BountyProgress mapResultSetToBountyProgress(final ResultSet resultSet)
      throws SQLException {
    return new BountyProgress(
        resultSet.getLong("id"),
        resultSet.getLong("user_id"),
        resultSet.getLong("day"),
        getLocalDate(resultSet, "acquisition_date"));
  }
}
