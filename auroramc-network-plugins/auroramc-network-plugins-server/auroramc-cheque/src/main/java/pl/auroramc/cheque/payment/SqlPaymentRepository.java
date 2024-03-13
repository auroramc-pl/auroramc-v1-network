package pl.auroramc.cheque.payment;

import static pl.auroramc.cheque.payment.SqlPaymentRepositoryQuery.CREATE_PAYMENT;
import static pl.auroramc.cheque.payment.SqlPaymentRepositoryQuery.CREATE_PAYMENT_SCHEMA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlPaymentRepository implements PaymentRepository {

  private final Juliet juliet;

  SqlPaymentRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createPaymentSchemaIfRequired() {
    try (
        final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()
    ) {
      statement.execute(CREATE_PAYMENT_SCHEMA);
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not create schema for payment entity, because of unexpected exception",
          exception
      );
    }
  }

  @Override
  public void createPayment(final Payment payment) {
    try (
        final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(CREATE_PAYMENT)
    ) {
      statement.setLong(1, payment.issuerId());
      statement.setLong(2, payment.retrieverId());
      statement.setBigDecimal(3, payment.amount());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new PaymentRepositoryException(
          "Could not create payment, because of unexpected exception",
          exception
      );
    }
  }
}
