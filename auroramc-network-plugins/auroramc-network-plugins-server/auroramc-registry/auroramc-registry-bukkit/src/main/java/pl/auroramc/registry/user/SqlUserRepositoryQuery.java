package pl.auroramc.registry.user;

import pl.auroramc.commons.sql.SqlQuery;

final class SqlUserRepositoryQuery {

  static final @SqlQuery String CREATE_USER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_users`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `unique_id` UUID NOT NULL,
          `username` VARCHAR(32) NOT NULL,
          UNIQUE INDEX `user_index_on_unique_id` (`unique_id`),
          UNIQUE INDEX `user_index_on_username` (`username`)
        );
      """;

  static final @SqlQuery String FIND_USER_BY_UNIQUE_ID =
      """
      SELECT
        `id`, `unique_id`, `username`
      FROM
        `auroramc_registry_users`
      WHERE
        `unique_id` = ?;
      """;

  static final @SqlQuery String FIND_USER_BY_USERNAME =
      """
      SELECT
        `id`, `unique_id`, `username`
      FROM
        `auroramc_registry_users`
      WHERE
        `username` = ?;
      """;

  static final @SqlQuery String CREATE_USER =
      """
      INSERT INTO
        `auroramc_registry_users`
        (`unique_id`, `username`)
      VALUES
        (?, ?);
      """;

  static final @SqlQuery String UPDATE_USER =
      """
      UPDATE
        `auroramc_registry_users`
      SET
        `username` = ?
      WHERE
        `id` = ?;
      """;

  private SqlUserRepositoryQuery() {}
}
