package pl.auroramc.economy.account;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.economy.account.SqlAccountRepositoryQuery.CREATE_ACCOUNT;
import static pl.auroramc.economy.account.SqlAccountRepositoryQuery.CREATE_ACCOUNT_INDEX_FOR_BALANCE;
import static pl.auroramc.economy.account.SqlAccountRepositoryQuery.CREATE_ACCOUNT_SCHEMA;
import static pl.auroramc.economy.account.SqlAccountRepositoryQuery.DELETE_ACCOUNT;
import static pl.auroramc.economy.account.SqlAccountRepositoryQuery.FIND_ACCOUNT_BY_USER_ID_AND_CURRENCY_ID;
import static pl.auroramc.economy.account.SqlAccountRepositoryQuery.UPDATE_ACCOUNT;
import static pl.auroramc.economy.account.SqlAccountRepositoryUtils.getDigitCountBeforeDecimalPoint;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

class SqlAccountRepository implements AccountRepository {

  public static final int MAXIMUM_BALANCE_DIGIT_COUNT_BEFORE_DECIMAL_POINT = 9;
  private final Logger logger;
  private final Juliet juliet;

  SqlAccountRepository(final Logger logger, final Juliet juliet) {
    this.logger = logger;
    this.juliet = juliet;
  }

  void createAccountSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_ACCOUNT_SCHEMA);
      statement.execute(CREATE_ACCOUNT_INDEX_FOR_BALANCE);
    } catch (final SQLException exception) {
      throw new AccountRepositoryException(
          "Could not create schema for account entity, because of unexpected exception", exception);
    }
  }

  @Override
  public Account findAccountByUserIdAndCurrencyId(final Long userId, final Long currencyId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(FIND_ACCOUNT_BY_USER_ID_AND_CURRENCY_ID)) {
      statement.setLong(1, userId);
      statement.setLong(2, currencyId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToAccount(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new AccountRepositoryException(
          "Could not find account identified by %d for %d currency, because of unexpected exception"
              .formatted(userId, currencyId),
          exception);
    }
  }

  @Override
  public void createAccount(final Account account) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_ACCOUNT, RETURN_GENERATED_KEYS)) {
      statement.setLong(1, account.getUserId());
      statement.setLong(2, account.getCurrencyId());
      statement.setBigDecimal(3, account.getBalance());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          account.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new AccountRepositoryException(
          "Could not create account identified by %d for %d currency, because of unexpected exception"
              .formatted(account.getUserId(), account.getCurrencyId()),
          exception);
    }
  }

  @Override
  public void updateAccount(final Account account) {
    if (getDigitCountBeforeDecimalPoint(account.getBalance())
        > MAXIMUM_BALANCE_DIGIT_COUNT_BEFORE_DECIMAL_POINT) {
      throw new AccountRepositoryException(
          "Could not update account identified by %d for %d currency, because balance overflows over table definition"
              .formatted(account.getUserId(), account.getCurrencyId()));
    }

    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_ACCOUNT)) {
      statement.setBigDecimal(1, account.getBalance());
      statement.setLong(2, account.getUserId());
      statement.setLong(3, account.getCurrencyId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new AccountRepositoryException(
          "Could not update account identified by %d for %d currency, because of unexpected exception"
              .formatted(account.getUserId(), account.getCurrencyId()),
          exception);
    }
  }

  @Override
  public void deleteAccount(final Account account) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT)) {
      statement.setLong(1, account.getUserId());
      statement.setLong(2, account.getCurrencyId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new AccountRepositoryException(
          "Could not delete account identified by %d for %d currency, because of unexpected exception"
              .formatted(account.getUserId(), account.getCurrencyId()),
          exception);
    }
  }

  @Override
  public void transferOfBalance(
      final Account initiatorAccount,
      final Account receivingAccount,
      final Long currencyId,
      final BigDecimal amount) {
    try (final Connection connection = juliet.borrowConnection()) {
      executeTransactionForTransferOfBalance(
          connection, initiatorAccount, receivingAccount, currencyId, amount);
    } catch (final SQLException exception) {
      throw new AccountRepositoryException(
          "Could not transfer %.2f from %d to %d for %d currency, because of unexpected exception"
              .formatted(
                  amount, initiatorAccount.getUserId(), receivingAccount.getUserId(), currencyId),
          exception);
    }
  }

  private void executeTransactionForTransferOfBalance(
      final Connection connection,
      final Account initiatorAccount,
      final Account receivingAccount,
      final Long currencyId,
      final BigDecimal amount)
      throws SQLException {
    try (final PreparedStatement depositionStatement = connection.prepareStatement(UPDATE_ACCOUNT);
        final PreparedStatement withdrawalStatement = connection.prepareStatement(UPDATE_ACCOUNT)) {
      connection.setAutoCommit(false);

      final long initiatorStamp = initiatorAccount.getLock().writeLock();
      final long receivingStamp = receivingAccount.getLock().writeLock();

      final BigDecimal newBalanceForInitiatorAccount =
          initiatorAccount.getBalance().subtract(amount);
      final BigDecimal newBalanceForReceivingAccount = receivingAccount.getBalance().add(amount);
      try {
        setParametersForTransferOfBalance(
            depositionStatement,
            initiatorAccount.getUserId(),
            currencyId,
            newBalanceForInitiatorAccount);
        depositionStatement.executeUpdate();

        setParametersForTransferOfBalance(
            withdrawalStatement,
            receivingAccount.getUserId(),
            currencyId,
            newBalanceForReceivingAccount);
        withdrawalStatement.executeUpdate();

        connection.commit();

        initiatorAccount.setBalance(newBalanceForInitiatorAccount);
        receivingAccount.setBalance(newBalanceForReceivingAccount);
      } finally {
        receivingAccount.getLock().unlockWrite(receivingStamp);
        initiatorAccount.getLock().unlockWrite(initiatorStamp);
      }
    } catch (final SQLException exception) {
      delegateCaughtException(logger, exception);
      try {
        logger.info(
            "Rolling back transaction for transfer of balance from %d to %d for %d currency with value of %.2f"
                .formatted(
                    initiatorAccount.getUserId(),
                    receivingAccount.getUserId(),
                    currencyId,
                    amount));
        connection.rollback();
      } catch (final SQLException exceptionFromRollback) {
        throw new AccountRepositoryException(
            "Could not rollback transaction, because of unexpected exception",
            exceptionFromRollback);
      }
    }
  }

  private void setParametersForTransferOfBalance(
      final PreparedStatement statement,
      final Long userId,
      final Long currencyId,
      final BigDecimal balance)
      throws SQLException {
    statement.setBigDecimal(1, balance);
    statement.setLong(2, userId);
    statement.setLong(3, currencyId);
  }

  private Account mapResultSetToAccount(final ResultSet resultSet) throws SQLException {
    return new Account(
        resultSet.getLong("id"),
        resultSet.getLong("user_id"),
        resultSet.getLong("currency_id"),
        resultSet.getBigDecimal("balance"));
  }
}
