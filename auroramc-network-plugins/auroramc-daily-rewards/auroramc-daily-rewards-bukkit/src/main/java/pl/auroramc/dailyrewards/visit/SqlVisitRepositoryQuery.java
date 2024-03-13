package pl.auroramc.dailyrewards.visit;

final class SqlVisitRepositoryQuery {

  static final String CREATE_VISIT_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_dailyrewards_visits` (
          `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
          `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
          `session_duration` BIGINT NOT NULL,
          `session_start_time` TIMESTAMP NOT NULL,
          `session_ditch_time` TIMESTAMP NOT NULL
        );
      """;

  static final String CREATE_VISIT =
      """
      INSERT INTO `auroramc_dailyrewards_visits`
        (`user_id`, `session_duration`, `session_start_time`, `session_ditch_time`)
      VALUES
        (?, ?, ?, ?);
      """;

  static final String FIND_VISITS_BY_USER_ID =
      """
      SELECT
        `user_id`,
        `session_duration`,
        `session_start_time`,
        `session_ditch_time`
      FROM
        `auroramc_dailyrewards_visits`
      WHERE
        `user_id` = ?
      LIMIT
        20;
      """;

  static final String FIND_VISITS_BY_USER_ID_BETWEEN =
      """
      SELECT
        `user_id`,
        `session_duration`,
        `session_start_time`,
        `session_ditch_time`
      FROM
        `auroramc_dailyrewards_visits`
      WHERE
        `user_id` = ?
      AND
        `session_start_time` BETWEEN ? AND ?
      LIMIT
        20;
      """;

  private SqlVisitRepositoryQuery() {

  }
}
