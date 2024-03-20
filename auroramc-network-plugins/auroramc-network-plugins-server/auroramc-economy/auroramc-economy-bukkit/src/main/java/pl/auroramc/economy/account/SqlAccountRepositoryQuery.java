package pl.auroramc.economy.account;

final class SqlAccountRepositoryQuery {

  static final String CREATE_ACCOUNT_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_economy_accounts`
        (
          `user_id` BIGINT NOT NULL,
          `currency_id` BIGINT NOT NULL,
          `balance` DECIMAL(11, 2) NOT NULL,
          FOREIGN KEY
            (`user_id`)
            REFERENCES
            `auroramc_registry_users` (`id`)
            ON DELETE CASCADE,
          FOREIGN KEY
            (`currency_id`)
          REFERENCES
            `auroramc_economy_currencies` (`id`)
            ON DELETE CASCADE,
          CONSTRAINT
            `account_balance_greater_than_zero`
            CHECK (
              `balance` >= 0
            )
        );
      """;

  static final String CREATE_ACCOUNT_INDEX_FOR_BALANCE =
      """
      CREATE INDEX IF NOT EXISTS
        `auroramc_economy_accounts_index_on_balance`
      ON
        `auroramc_economy_accounts`
        (`balance` DESC);
      """;

  static final String FIND_ACCOUNT_BY_USER_ID_AND_CURRENCY_ID =
      """
      SELECT
        `user_id`, `currency_id`, `balance`
      FROM
        `auroramc_economy_accounts`
      WHERE
        `user_id` = ? AND `currency_id` = ?;
      """;

  static final String CREATE_ACCOUNT =
      """
      INSERT INTO
        `auroramc_economy_accounts`
        (`user_id`, `currency_id`, `balance`)
      VALUES
        (?, ?, ?);
      """;

  static final String UPDATE_ACCOUNT =
      """
      UPDATE
        `auroramc_economy_accounts`
      SET
        `balance` = ?
      WHERE
        `user_id` = ? AND `currency_id` = ?;
      """;

  static final String DELETE_ACCOUNT =
      """
      DELETE FROM
        `auroramc_economy_accounts`
      WHERE
        `user_id` = ? AND `currency_id` = ?;
      """;

  private SqlAccountRepositoryQuery() {}
}
