package pl.auroramc.registry.observer;

import static pl.auroramc.registry.observer.SqlObserverRepositoryQuery.CREATE_OBSERVER;
import static pl.auroramc.registry.observer.SqlObserverRepositoryQuery.CREATE_OBSERVER_SCHEMA;
import static pl.auroramc.registry.observer.SqlObserverRepositoryQuery.FIND_OBSERVER_BY_PROVIDER_ID_AND_USER_ID;
import static pl.auroramc.registry.observer.SqlObserverRepositoryQuery.UPDATE_OBSERVER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlObserverRepository implements ObserverRepository {

  private final Juliet juliet;

  SqlObserverRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createObserverSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_OBSERVER_SCHEMA);
    } catch (final SQLException exception) {
      throw new ObserverRepositoryException(
          "Could not create observer schema, becasue of unexpected exception.", exception);
    }
  }

  @Override
  public Observer findObserverByProviderIdAndUserId(final Long providerId, final Long userId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(FIND_OBSERVER_BY_PROVIDER_ID_AND_USER_ID)) {
      statement.setLong(1, providerId);
      statement.setLong(2, userId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToObserver(resultSet);
        }
      }
    } catch (final SQLException exception) {
      throw new ObserverRepositoryException(
          "Could not find observer, because of unexpected exception.", exception);
    }
    return null;
  }

  @Override
  public void createObserver(final Observer observer) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_OBSERVER)) {
      statement.setLong(1, observer.getProviderId());
      statement.setLong(2, observer.getUserId());
      statement.setBoolean(3, observer.isEnabled());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ObserverRepositoryException(
          "Could not create observer, because of unexpected exception.", exception);
    }
  }

  @Override
  public void updateObserver(final Observer observer) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_OBSERVER)) {
      statement.setBoolean(1, observer.isEnabled());
      statement.setLong(2, observer.getProviderId());
      statement.setLong(3, observer.getUserId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ObserverRepositoryException(
          "Could not update observer, because of unexpected exception.", exception);
    }
  }

  private Observer mapResultSetToObserver(final ResultSet resultSet) throws SQLException {
    return new Observer(
        resultSet.getLong("id"),
        resultSet.getLong("provider_id"),
        resultSet.getLong("user_id"),
        resultSet.getBoolean("enabled"));
  }
}
