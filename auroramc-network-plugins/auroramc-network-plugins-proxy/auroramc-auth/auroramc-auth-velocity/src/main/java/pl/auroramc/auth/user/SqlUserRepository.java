package pl.auroramc.auth.user;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.CREATE_UNIQUE_INDEX_ON_UNIQUE_ID;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.CREATE_UNIQUE_INDEX_ON_USERNAME;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.CREATE_USER;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.CREATE_USER_SCHEMA;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.DELETE_USER;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.FIND_USER_BY_EMAIL;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.FIND_USER_BY_UNIQUE_ID;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.FIND_USER_BY_USERNAME;
import static pl.auroramc.auth.user.SqlUserRepositoryQuery.UPDATE_USER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
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
      statement.execute(CREATE_USER_SCHEMA);
      statement.execute(CREATE_UNIQUE_INDEX_ON_UNIQUE_ID);
      statement.execute(CREATE_UNIQUE_INDEX_ON_USERNAME);
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not create schema for user entity, because of unexpected exception.",
          exception);
    }
  }

  @Override
  public Optional<User> findUserByUniqueId(final UUID uniqueId) {
    return findUserWithQuery(FIND_USER_BY_UNIQUE_ID, uniqueId.toString());
  }

  @Override
  public Optional<User> findUserByUsername(final String username) {
    return findUserWithQuery(FIND_USER_BY_USERNAME, username);
  }

  @Override
  public Optional<User> findUserByEmail(final String email) {
    return findUserWithQuery(FIND_USER_BY_EMAIL, email);
  }

  private Optional<User> findUserWithQuery(final String query, final String parameter) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(query)
    ) {
      statement.setString(1, parameter);
      try (final ResultSet resultSet = statement.executeQuery()) {
        return mapResultSetToUser(resultSet);
      }
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not find user, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void createUser(final User user) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_USER, RETURN_GENERATED_KEYS)
    ) {
      statement.setString(1, user.getUniqueId().toString());
      statement.setString(2, user.getUsername());
      statement.setString(3, user.getPassword());
      statement.setString(4, Optional.ofNullable(user.getPremiumUniqueId()).map(UUID::toString).orElse(null));
      statement.setString(5, user.getEmail());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          user.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not create user, because of unexpected exception.",
          exception
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
      statement.setString(2, user.getPassword());
      statement.setString(3, Optional.ofNullable(user.getPremiumUniqueId()).map(UUID::toString).orElse(null));
      statement.setString(4, user.getEmail());
      statement.setLong(5, user.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not update user, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void deleteUser(final User user) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_USER)
    ) {
      statement.setLong(1, user.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new UserRepositoryException(
          "Could not delete user, because of unexpected exception.",
          exception
      );
    }
  }

  private Optional<User> mapResultSetToUser(final ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      return Optional.of(
          new User(
              resultSet.getLong("id"),
              UUID.fromString(resultSet.getString("unique_id")),
              resultSet.getString("username"),
              resultSet.getString("password"),
              resultSet.getString("email"),
              Optional.ofNullable(resultSet.getString("premium_unique_id")).map(UUID::fromString).orElse(null),
              false
          )
      );
    }

    return Optional.empty();
  }
}
