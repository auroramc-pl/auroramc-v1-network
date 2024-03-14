package pl.auroramc.auctions.vault;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.auctions.vault.SqlVaultRepositoryQuery.CREATE_VAULT_SCHEMA;
import static pl.auroramc.auctions.vault.SqlVaultRepositoryQuery.DELETE_VAULT;
import static pl.auroramc.auctions.vault.SqlVaultRepositoryQuery.FIND_VAULT_BY_USER_ID;
import static pl.auroramc.auctions.vault.SqlVaultRepositoryQuery.CREATE_VAULT;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlVaultRepository implements VaultRepository {

  private final Juliet juliet;

  SqlVaultRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createVaultSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_VAULT_SCHEMA);
    } catch (final SQLException exception) {
      throw new VaultRepositoryException(
          "Could not create schema for vault entity, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public Vault getVaultByUserId(final Long userId) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_VAULT_BY_USER_ID)
    ) {
      statement.setLong(1, userId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToVault(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new VaultRepositoryException(
          "Could not find vault by user id, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void createVault(final Vault vault) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_VAULT, RETURN_GENERATED_KEYS)
    ) {
      statement.setLong(1, vault.getUserId());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          vault.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new VaultRepositoryException(
          "Could not create vault, because of unexpected exception.",
          exception
      );
    }
  }

  @Override
  public void deleteVault(final Vault vault) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_VAULT)
    ) {
      statement.setLong(1, vault.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new VaultRepositoryException(
          "Could not delete vault, because of unexpected exception.",
          exception
      );
    }
  }

  private Vault mapResultSetToVault(final ResultSet resultSet) throws SQLException {
    return new Vault(
        resultSet.getLong("id"),
        resultSet.getLong("user_id")
    );
  }
}
