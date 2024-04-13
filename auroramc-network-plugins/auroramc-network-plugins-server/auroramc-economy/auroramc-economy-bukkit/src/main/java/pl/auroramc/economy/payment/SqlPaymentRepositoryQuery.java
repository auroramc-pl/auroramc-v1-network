package pl.auroramc.economy.payment;

final class SqlPaymentRepositoryQuery {

  static final String CREATE_PAYMENT_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_economy_payments`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `initiator_id` BIGINT NOT NULL REFERENCES `auroramc_registry_users`(`id`) ON DELETE CASCADE,
          `receiver_id` BIGINT NOT NULL REFERENCES `auroramc_registry_users`(`id`) ON DELETE CASCADE,
          `currency_id` BIGINT NOT NULL REFERENCES `auroramc_economy_currencies`(`id`) ON DELETE CASCADE,
          `amount` DECIMAL(11, 2) NOT NULL,
          `transaction_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
      """;

  static final String FIND_PAYMENT_BY_ID =
      """
      SELECT
        `id`, `initiator_id`, `receiver_id`, `currency_id`, `amount`, `transaction_time`
      FROM
        `auroramc_economy_payments`
      WHERE
        `id` = ?;
      """;

  static final String FIND_PAYMENT_SUMMARIES_BY_INITIATOR_ID =
      """
      SELECT
        `payment`.`id`,
        `initiator`.`username` AS `initiator_username`,
        `receiver`.`username` AS `receiver_username`,
        `currency`.`symbol` AS `currency_symbol`,
        `payment`.`amount`,
        `payment`.`transaction_time`
      FROM
        `auroramc_economy_payments` AS `payment`
      LEFT JOIN
        `auroramc_registry_users` AS `initiator`
      ON
        `initiator_id` = `initiator`.`id`
      LEFT JOIN
        `auroramc_registry_users` AS `receiver`
      ON
        `receiver_id` = `receiver`.`id`
      LEFT JOIN
        `auroramc_economy_currencies` AS `currency`
      ON
        `currency_id` = `currency`.`id`
      WHERE
        `initiator_id` = ?
      ORDER BY
        `transaction_time` DESC
      LIMIT
        20;
      """;

  static final String FIND_PAYMENT_SUMMARIES_BY_RECEIVER_ID =
      """
      SELECT
        `payment`.`id`,
        `initiator`.`username` AS `initiator_username`,
        `receiver`.`username` AS `receiver_username`,
        `currency`.`symbol` AS `currency_symbol`,
        `payment`.`amount`,
        `payment`.`transaction_time`
      FROM
        `auroramc_economy_payments` AS `payment`
      LEFT JOIN
        `auroramc_registry_users` AS `initiator`
      ON
        `initiator_id` = `initiator`.`id`
      LEFT JOIN
        `auroramc_registry_users` AS `receiver`
      ON
        `receiver_id` = `receiver`.`id`
      LEFT JOIN
        `auroramc_economy_currencies` AS `currency`
      ON
        `currency_id` = `currency`.`id`
      WHERE
        `receiver_id` = ?
      ORDER BY
        `transaction_time` DESC
      LIMIT
        20;
      """;

  static final String CREATE_PAYMENT =
      """
      INSERT INTO
        `auroramc_economy_payments`
        (`initiator_id`, `receiver_id`, `currency_id`, `amount`, `transaction_time`)
      VALUES
        (?, ?, ?, ?, ?);
      """;

  static final String DELETE_PAYMENT =
      """
      DELETE FROM
        `auroramc_economy_payments`
      WHERE
        `id` = ?;
      """;

  private SqlPaymentRepositoryQuery() {}
}
