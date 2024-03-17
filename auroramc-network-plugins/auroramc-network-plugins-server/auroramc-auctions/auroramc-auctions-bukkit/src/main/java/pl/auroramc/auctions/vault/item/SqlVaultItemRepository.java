package pl.auroramc.auctions.vault.item;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.auctions.vault.item.SqlVaultItemRepositoryQuery.CREATE_VAULT_ITEM_SCHEMA;
import static pl.auroramc.auctions.vault.item.SqlVaultItemRepositoryQuery.DELETE_VAULT_ITEM;
import static pl.auroramc.auctions.vault.item.SqlVaultItemRepositoryQuery.FIND_VAULT_ITEMS_BY_USER_ID;
import static pl.auroramc.auctions.vault.item.SqlVaultItemRepositoryQuery.CREATE_VAULT_ITEM;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import moe.rafal.juliet.Juliet;

class SqlVaultItemRepository implements VaultItemRepository {

  private final Juliet juliet;

  SqlVaultItemRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createVaultItemSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_VAULT_ITEM_SCHEMA);
    } catch (final SQLException exception) {
      throw new VaultItemRepositoryException(
          "Could not create schema for vault item entity, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public Set<VaultItem> findVaultItemsByUserId(final Long userId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_VAULT_ITEMS_BY_USER_ID)
    ) {
      statement.setLong(1, userId);

      final Set<VaultItem> vaultItems = new HashSet<>();
      try (final ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          vaultItems.add(mapResultSetToVaultItem(resultSet));
        }
      }

      return vaultItems;
    } catch (final SQLException exception) {
      throw new VaultItemRepositoryException(
          "Could not find vault items by vault, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void createVaultItem(final VaultItem vaultItem) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_VAULT_ITEM, RETURN_GENERATED_KEYS)
    ) {
      statement.setLong(1, vaultItem.getUserId());
      statement.setLong(2, vaultItem.getVaultId());
      statement.setBytes(3, vaultItem.getSubject());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          vaultItem.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new VaultItemRepositoryException(
          "Could not create vault item, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void deleteVaultItem(final VaultItem vaultItem) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_VAULT_ITEM)
    ) {
      statement.setLong(1, vaultItem.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new VaultItemRepositoryException(
          "Could not delete vault item, because of unexpected exception.",
          exception
      );
    }
  }

  private VaultItem mapResultSetToVaultItem(final ResultSet resultSet) throws SQLException {
    return new VaultItem(
        resultSet.getLong("id"),
        resultSet.getLong("user_id"),
        resultSet.getLong("vault_id"),
        resultSet.getBytes("subject")
    );
  }
}
