package pl.auroramc.registry.user;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.registry.user.SqlUserRepositoryQuery.CREATE_USER;
import static pl.auroramc.registry.user.SqlUserRepositoryQuery.CREATE_USER_SCHEMA_IF_REQUIRED;
import static pl.auroramc.registry.user.SqlUserRepositoryQuery.FIND_USER_BY_UNIQUE_ID;
import static pl.auroramc.registry.user.SqlUserRepositoryQuery.FIND_USER_BY_USERNAME;
import static pl.auroramc.registry.user.SqlUserRepositoryQuery.UPDATE_USER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import moe.rafal.juliet.Juliet;

class SqlUserRepository implements UserRepository {

  private final Juliet juliet;

  SqlUserRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createUserSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_USER_SCHEMA_IF_REQUIRED);
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not create user schema, because of unexpected exception.", exception
      );
    }
  }

  @Override
  public User findUserByUniqueId(final UUID uniqueId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_UNIQUE_ID)
    ) {
      statement.setObject(1, uniqueId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToUser(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not find user by unique id, because of unexpected exception.", exception
      );
    }
  }

  @Override
  public User findUserByUsername(final String username) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_USER_BY_USERNAME)
    ) {
      statement.setString(1, username);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToUser(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not find user by username, because of unexpected exception.", exception
      );
    }
  }

  @Override
  public void createUser(final User user) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_USER, RETURN_GENERATED_KEYS)
    ) {
      statement.setObject(1, user.getUniqueId());
      statement.setString(2, user.getUsername());
      statement.executeUpdate();
      try (final ResultSet resultSet = statement.getGeneratedKeys()) {
        if (resultSet.next()) {
          user.setId(resultSet.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not create user, because of unexpected exception.", exception
      );
    }
  }

  @Override
  public void updateUser(final User user) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_USER)
    ) {
      statement.setString(1, user.getUsername());
      statement.setLong(2, user.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not update user, because of unexpected exception.", exception
      );
    }
  }

  private User mapResultSetToUser(final ResultSet resultSet) throws SQLException {
    return new User(
        resultSet.getLong("id"),
        UUID.fromString(resultSet.getString("unique_id")),
        resultSet.getString("username")
    );
  }
}
