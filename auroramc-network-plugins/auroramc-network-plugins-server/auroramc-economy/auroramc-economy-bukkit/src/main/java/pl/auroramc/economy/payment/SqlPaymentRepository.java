package pl.auroramc.economy.payment;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static pl.auroramc.economy.payment.SqlPaymentRepositoryQuery.CREATE_PAYMENT;
import static pl.auroramc.economy.payment.SqlPaymentRepositoryQuery.CREATE_PAYMENT_SCHEMA;
import static pl.auroramc.economy.payment.SqlPaymentRepositoryQuery.DELETE_PAYMENT;
import static pl.auroramc.economy.payment.SqlPaymentRepositoryQuery.FIND_PAYMENT_BY_ID;
import static pl.auroramc.economy.payment.SqlPaymentRepositoryQuery.FIND_PAYMENT_SUMMARIES_BY_INITIATOR_ID;
import static pl.auroramc.economy.payment.SqlPaymentRepositoryQuery.FIND_PAYMENT_SUMMARIES_BY_RECEIVER_ID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import moe.rafal.juliet.Juliet;

class SqlPaymentRepository implements PaymentRepository {

  private final Juliet juliet;

  SqlPaymentRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createPaymentSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_PAYMENT_SCHEMA);
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not create schema for payment entity, because of unexpected exception", exception);
    }
  }

  @Override
  public Payment findPaymentById(final Long paymentId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_PAYMENT_BY_ID)) {
      statement.setLong(1, paymentId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToPayment(resultSet);
        }
      }
      return null;
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not find payment identified by %d, because of unexpected exception"
              .formatted(paymentId),
          exception);
    }
  }

  @Override
  public List<PaymentSummary> findPaymentSummariesByInitiatorId(final Long initiatorId) {
    return findPaymentsByReferencingId(FIND_PAYMENT_SUMMARIES_BY_INITIATOR_ID, initiatorId);
  }

  @Override
  public List<PaymentSummary> findPaymentSummariesByReceiverId(final Long receiverId) {
    return findPaymentsByReferencingId(FIND_PAYMENT_SUMMARIES_BY_RECEIVER_ID, receiverId);
  }

  private List<PaymentSummary> findPaymentsByReferencingId(
      final String query, final Long referencingId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setLong(1, referencingId);

      final List<PaymentSummary> resolvedPayments = new ArrayList<>();
      try (final ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          resolvedPayments.add(mapResultSetToPaymentSummary(resultSet));
        }
      }

      return resolvedPayments;
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not find payments with referencing id of %d, because of unexpected exception"
              .formatted(referencingId),
          exception);
    }
  }

  @Override
  public void createPayment(final Payment payment) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_PAYMENT, RETURN_GENERATED_KEYS)) {
      statement.setLong(1, payment.getInitiatorId());
      statement.setLong(2, payment.getReceiverId());
      statement.setLong(3, payment.getCurrencyId());
      statement.setBigDecimal(4, payment.getAmount());
      statement.setTimestamp(5, Timestamp.from(payment.getTransactionTime()));
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          payment.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not create payment identified by %d, because of unexpected exception"
              .formatted(payment.getId()),
          exception);
    }
  }

  @Override
  public void deletePayment(final Payment payment) {
    checkNotNull(payment.getId());
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(DELETE_PAYMENT)) {
      statement.setLong(1, payment.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not delete payment identified by %d, because of unexpected exception"
              .formatted(payment.getId()),
          exception);
    }
  }

  private Payment mapResultSetToPayment(final ResultSet resultSet) throws SQLException {
    return new Payment(
        resultSet.getLong("id"),
        resultSet.getLong("initiator_id"),
        resultSet.getLong("receiver_id"),
        resultSet.getLong("currency_id"),
        resultSet.getBigDecimal("amount"),
        resultSet.getTimestamp("transaction_time").toInstant());
  }

  private PaymentSummary mapResultSetToPaymentSummary(final ResultSet resultSet)
      throws SQLException {
    return new PaymentSummary(
        resultSet.getLong("id"),
        resultSet.getString("initiator_username"),
        resultSet.getString("receiver_username"),
        resultSet.getString("currency_symbol"),
        resultSet.getBigDecimal("amount"),
        resultSet.getTimestamp("transaction_time").toInstant());
  }
}
