package pl.auroramc.registry.resource.key;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.registry.resource.key.SqlResourceKeyRepositoryQuery.CREATE_RESOURCE_KEY;
import static pl.auroramc.registry.resource.key.SqlResourceKeyRepositoryQuery.CREATE_RESOURCE_KEYS_SCHEMA;
import static pl.auroramc.registry.resource.key.SqlResourceKeyRepositoryQuery.DELETE_RESOURCE_KEY;
import static pl.auroramc.registry.resource.key.SqlResourceKeyRepositoryQuery.GET_RESOURCE_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import moe.rafal.juliet.Juliet;

class SqlResourceKeyRepository implements ResourceKeyRepository {

  private final Juliet juliet;

  SqlResourceKeyRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createResourceKeysSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_RESOURCE_KEYS_SCHEMA);
    } catch (final SQLException exception) {
      throw new ResourceKeyRepositoryException(
          "Could not create schema for resource keys, because of unexpected exception.", exception);
    }
  }

  @Override
  public List<ResourceKey> getResourceKeys() {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(GET_RESOURCE_KEYS)) {
      final List<ResourceKey> results = new ArrayList<>();
      final ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        results.add(mapResultSetToResourceKey(resultSet));
      }
      return results;
    } catch (final SQLException exception) {
      throw new ResourceKeyRepositoryException(
          "Could not get resource keys, because of unexpected exception.", exception);
    }
  }

  @Override
  public void createResourceKeys(final List<ResourceKey> resourceKeys) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_RESOURCE_KEY, RETURN_GENERATED_KEYS)) {
      for (final ResourceKey resourceKey : resourceKeys) {
        statement.setString(1, resourceKey.getName());
        statement.addBatch();
      }
      statement.executeBatch();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        for (final ResourceKey resourceKey : resourceKeys) {
          if (generatedKeys.next()) {
            resourceKey.setId(generatedKeys.getLong(1));
          }
        }
      }
    } catch (final SQLException exception) {
      throw new ResourceKeyRepositoryException(
          "Could not create resource keys, because of unexpected exception.", exception);
    }
  }

  @Override
  public void deleteResourceKeys(final List<ResourceKey> resourceKeys) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_RESOURCE_KEY)) {
      for (final ResourceKey resourceKey : resourceKeys) {
        statement.setLong(1, resourceKey.getId());
        statement.addBatch();
      }
      statement.executeBatch();
    } catch (final SQLException exception) {
      throw new ResourceKeyRepositoryException(
          "Could not delete resource keys, because of unexpected exception.", exception);
    }
  }

  private ResourceKey mapResultSetToResourceKey(final ResultSet resultSet) throws SQLException {
    return new ResourceKey(resultSet.getLong("id"), resultSet.getString("name"));
  }
}
