package pl.auroramc.cheque.payment;

final class SqlPaymentRepositoryQuery {

  static final String CREATE_PAYMENT_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_cheque_payments` (
        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
        `issuer_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
        `retriever_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
        `amount` DECIMAL(11, 2) NOT NULL,
        `finalization_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
      );
      """;

  static final String CREATE_PAYMENT =
      """
      INSERT INTO
        `auroramc_cheque_payments`
        (`issuer_id`, `retriever_id`, `amount`)
      VALUES
        (?, ?, ?)
      """;

  private SqlPaymentRepositoryQuery() {}
}
