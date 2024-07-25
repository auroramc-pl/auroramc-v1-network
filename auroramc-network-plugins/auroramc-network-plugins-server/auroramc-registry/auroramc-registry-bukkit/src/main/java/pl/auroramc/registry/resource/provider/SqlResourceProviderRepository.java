package pl.auroramc.registry.resource.provider;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.registry.resource.provider.SqlResourceProviderRepositoryQuery.CREATE_INDEX_ON_RESOURCE_PROVIDER_NAME;
import static pl.auroramc.registry.resource.provider.SqlResourceProviderRepositoryQuery.CREATE_RESOURCE_PROVIDER;
import static pl.auroramc.registry.resource.provider.SqlResourceProviderRepositoryQuery.CREATE_RESOURCE_PROVIDER_SCHEMA;
import static pl.auroramc.registry.resource.provider.SqlResourceProviderRepositoryQuery.FIND_RESOURCE_PROVIDER_BY_NAME;
import static pl.auroramc.registry.resource.provider.SqlResourceProviderRepositoryQuery.UPDATE_RESOURCE_PROVIDER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlResourceProviderRepository implements ResourceProviderRepository {

  private final Juliet juliet;

  SqlResourceProviderRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createResourceProviderSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_RESOURCE_PROVIDER_SCHEMA);
      statement.execute(CREATE_INDEX_ON_RESOURCE_PROVIDER_NAME);
    } catch (final SQLException exception) {
      throw new ResourceProviderRepositoryException(
          "Could not create resource provider schema, because of unexpected exception.", exception);
    }
  }

  @Override
  public ResourceProvider findResourceProviderByName(final String name) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(FIND_RESOURCE_PROVIDER_BY_NAME)) {
      statement.setString(1, name);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToResourceProvider(resultSet);
        }
      }
    } catch (final SQLException exception) {
      throw new ResourceProviderRepositoryException(
          "Could not find resource provider with name %s, because of unexpected exception."
              .formatted(name),
          exception);
    }
    return null;
  }

  @Override
  public void createResourceProvider(final ResourceProvider resourceProvider) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_RESOURCE_PROVIDER, RETURN_GENERATED_KEYS)) {
      statement.setString(1, resourceProvider.getName());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          resourceProvider.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new ResourceProviderRepositoryException(
          "Could not create resource provider with name %s, because of unexpected exception."
              .formatted(resourceProvider.getName()),
          exception);
    }
  }

  @Override
  public void updateResourceProvider(final ResourceProvider resourceProvider) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_RESOURCE_PROVIDER)) {
      statement.setString(1, resourceProvider.getName());
      statement.setLong(2, resourceProvider.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new ResourceProviderRepositoryException(
          "Could not update resource provider identified by %d, because of unexpected exception."
              .formatted(resourceProvider.getId()),
          exception);
    }
  }

  private ResourceProvider mapResultSetToResourceProvider(final ResultSet resultSet)
      throws SQLException {
    return new ResourceProvider(resultSet.getLong("id"), resultSet.getString("name"));
  }
}
