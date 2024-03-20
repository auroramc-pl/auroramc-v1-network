package pl.auroramc.economy.currency;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.economy.currency.SqlCurrencyRepositoryQuery.CREATE_CURRENCY;
import static pl.auroramc.economy.currency.SqlCurrencyRepositoryQuery.CREATE_CURRENCY_SCHEMA;
import static pl.auroramc.economy.currency.SqlCurrencyRepositoryQuery.DELETE_CURRENCY;
import static pl.auroramc.economy.currency.SqlCurrencyRepositoryQuery.FIND_CURRENCY_BY_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlCurrencyRepository implements CurrencyRepository {

  private final Juliet juliet;

  SqlCurrencyRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createCurrencySchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_CURRENCY_SCHEMA);
    } catch (final SQLException exception) {
      throw new CurrencyRepositoryException(
          "Could not create schema for currency entity, because of unexpected exception",
          exception);
    }
  }

  @Override
  public Currency findCurrencyById(final Long currencyId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_CURRENCY_BY_ID)) {
      statement.setLong(1, currencyId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToCurrency(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new CurrencyRepositoryException(
          "Could not find currency identified by %d, because of unexpected exception"
              .formatted(currencyId),
          exception);
    }
  }

  @Override
  public void createCurrency(final Currency currency) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_CURRENCY, RETURN_GENERATED_KEYS)) {
      statement.setString(1, currency.getName());
      statement.setString(2, currency.getSymbol());
      statement.setString(3, currency.getDescription());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          currency.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new CurrencyRepositoryException(
          "Could not create currency identified by %d, because of unexpected exception"
              .formatted(currency.getId()),
          exception);
    }
  }

  @Override
  public void deleteCurrency(final Currency currency) {
    checkNotNull(currency.getId());
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_CURRENCY)) {
      statement.setLong(1, currency.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new CurrencyRepositoryException(
          "Could not delete currency identified by %d, because of unexpected exception"
              .formatted(currency.getId()),
          exception);
    }
  }

  private Currency mapResultSetToCurrency(final ResultSet resultSet) throws SQLException {
    return new Currency(
        resultSet.getLong("id"),
        resultSet.getString("name"),
        resultSet.getString("symbol"),
        resultSet.getString("description"));
  }
}
