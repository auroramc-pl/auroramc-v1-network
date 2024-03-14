package pl.auroramc.auth.user;

final class SqlUserRepositoryQuery {

  static final String CREATE_USER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_auth_users` (
        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
        `unique_id` BINARY(36),
        `username` VARCHAR(16),
        `password` VARCHAR(72),
        `premium_unique_id` BINARY(36),
        `email` VARCHAR(255) DEFAULT NULL
      );
      """;

  static final String CREATE_UNIQUE_INDEX_ON_UNIQUE_ID =
      """
      CREATE UNIQUE INDEX IF NOT EXISTS
        `auroramc_auth_users_unique_id`
      ON
        `auroramc_auth_users` (`unique_id`);
      """;

  static final String CREATE_UNIQUE_INDEX_ON_USERNAME =
      """
      CREATE UNIQUE INDEX IF NOT EXISTS
        `auroramc_auth_users_username`
      ON
        `auroramc_auth_users` (`username`);
      """;

  static final String FIND_USER_BY_UNIQUE_ID =
      """
      SELECT
        `id`, `unique_id`, `username`, `password`, `premium_unique_id`, `email`
      FROM
        `auroramc_auth_users`
      WHERE
        `unique_id` = ?;
      """;

  static final String FIND_USER_BY_USERNAME =
      """
      SELECT
        `id`, `unique_id`, `username`, `password`, `premium_unique_id`, `email`
      FROM
        `auroramc_auth_users`
      WHERE
        `username` = ?;
      """;

  static final String FIND_USER_BY_EMAIL =
      """
      SELECT
        `id`, `unique_id`, `username`, `password`, `premium_unique_id`, `email`
      FROM
        `auroramc_auth_users`
      WHERE
        `email` = ?;
      """;

  static final String CREATE_USER =
      """
      INSERT INTO `auroramc_auth_users`
        (`unique_id`, `username`, `password`, `premium_unique_id`, `email`)
      VALUES
        (?, ?, ?, ?, ?);
      """;

  static final String UPDATE_USER =
      """
      UPDATE
        `auroramc_auth_users`
      SET
        `username` = ?,
        `password` = ?,
        `premium_unique_id` = ?,
        `email` = ?
      WHERE
        `id` = ?;
      """;

  static final String DELETE_USER =
      """
      DELETE FROM
        `auroramc_auth_users`
      WHERE
        `id` = ?;
      """;

  private SqlUserRepositoryQuery() {

  }
}
