package pl.auroramc.auctions.audience;

import static pl.auroramc.auctions.audience.SqlAudienceRepositoryQuery.CREATE_MESSAGE_VIEWER;
import static pl.auroramc.auctions.audience.SqlAudienceRepositoryQuery.CREATE_MESSAGE_VIEWER_SCHEMA;
import static pl.auroramc.auctions.audience.SqlAudienceRepositoryQuery.FIND_MESSAGE_VIEWER_BY_USER_UNIQUE_ID;
import static pl.auroramc.auctions.audience.SqlAudienceRepositoryQuery.UPDATE_MESSAGE_VIEWER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

class SqlAudienceRepository implements AudienceRepository {

  private final Juliet juliet;

  SqlAudienceRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createAudienceSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_MESSAGE_VIEWER_SCHEMA);
    } catch (final SQLException exception) {
      throw new AudienceRepositoryException(
          "Could not create schema for message viewer entity, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public Audience findAudienceByUniqueId(final UUID uniqueId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_MESSAGE_VIEWER_BY_USER_UNIQUE_ID)
    ) {
      statement.setObject(1, uniqueId.toString());
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToAudience(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new AudienceRepositoryException(
          "Could not find message viewer by user id, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public void createAudience(final Audience audience) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_MESSAGE_VIEWER)
    ) {
      statement.setLong(1, audience.getUserId());
      statement.setBoolean(2, audience.isAllowsMessages());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new AudienceRepositoryException(
          "Could not create message viewer, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void updateAudience(final Audience audience) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_MESSAGE_VIEWER)
    ) {
      statement.setBoolean(1, audience.isAllowsMessages());
      statement.setLong(2, audience.getUserId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new AudienceRepositoryException(
          "Could not update message viewer, because of unexpected exception.",
          exception
      );
    }
  }

  private Audience mapResultSetToAudience(final ResultSet resultSet) throws SQLException {
    return new Audience(
        resultSet.getLong("user_id"),
        resultSet.getBoolean("allows_messages")
    );
  }
}
