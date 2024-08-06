package pl.auroramc.registry.provider;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.registry.provider.SqlProviderRepositoryQuery.CREATE_INDEX_ON_PROVIDER_NAME;
import static pl.auroramc.registry.provider.SqlProviderRepositoryQuery.CREATE_PROVIDER;
import static pl.auroramc.registry.provider.SqlProviderRepositoryQuery.CREATE_PROVIDER_SCHEMA;
import static pl.auroramc.registry.provider.SqlProviderRepositoryQuery.FIND_PROVIDER_BY_NAME;
import static pl.auroramc.registry.provider.SqlProviderRepositoryQuery.UPDATE_PROVIDER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlProviderRepository implements ProviderRepository {

  private final Juliet juliet;

  SqlProviderRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createProviderSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_PROVIDER_SCHEMA);
      statement.execute(CREATE_INDEX_ON_PROVIDER_NAME);
    } catch (final SQLException exception) {
      throw new ProviderRepositoryException(
          "Could not create resource provider schema, because of unexpected exception.", exception);
    }
  }

  @Override
  public Provider findProviderByName(final String name) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_PROVIDER_BY_NAME)) {
      statement.setString(1, name);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToResourceProvider(resultSet);
        }
      }
    } catch (final SQLException exception) {
      throw new ProviderRepositoryException(
          "Could not find resource provider with name %s, because of unexpected exception."
              .formatted(name),
          exception);
    }
    return null;
  }

  @Override
  public void createProvider(final Provider provider) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_PROVIDER, RETURN_GENERATED_KEYS)) {
      statement.setString(1, provider.getName());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          provider.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new ProviderRepositoryException(
          "Could not create resource provider with name %s, because of unexpected exception."
              .formatted(provider.getName()),
          exception);
    }
  }

  @Override
  public void updateProvider(final Provider provider) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_PROVIDER)) {
      statement.setString(1, provider.getName());
      statement.setLong(2, provider.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ProviderRepositoryException(
          "Could not update resource provider identified by %d, because of unexpected exception."
              .formatted(provider.getId()),
          exception);
    }
  }

  private Provider mapResultSetToResourceProvider(final ResultSet resultSet) throws SQLException {
    return new Provider(resultSet.getLong("id"), resultSet.getString("name"));
  }
}
