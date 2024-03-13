package pl.auroramc.economy.currency;

final class SqlCurrencyRepositoryQuery {

  static final String CREATE_CURRENCY_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_economy_currencies`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `name` VARCHAR(64) NOT NULL,
          `symbol` CHAR(1) NOT NULL,
          `description` VARCHAR(512) NOT NULL,
          CONSTRAINT `currency_unique_on_name` UNIQUE (`name`),
          CONSTRAINT `currency_unique_on_symbol` UNIQUE (`symbol`)
        );
      """;

  static final String FIND_CURRENCY_BY_ID =
      """
      SELECT
        `id`, `name`, `description`, `symbol`
      FROM
        `auroramc_economy_currencies`
      WHERE
        `id` = ?;
      """;

  static final String CREATE_CURRENCY =
      """
      INSERT INTO
        `auroramc_economy_currencies`
        (`name`, `symbol`, `description`)
      VALUES
        (?, ?, ?);
      """;

  static final String DELETE_CURRENCY =
      """
      DELETE FROM
        `auroramc_economy_currencies`
      WHERE
        `id` = ?;
      """;

  private SqlCurrencyRepositoryQuery() {

  }
}
