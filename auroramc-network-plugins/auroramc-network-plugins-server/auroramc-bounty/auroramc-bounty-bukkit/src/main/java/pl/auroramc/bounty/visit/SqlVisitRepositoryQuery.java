package pl.auroramc.bounty.visit;

final class SqlVisitRepositoryQuery {

  static final String CREATE_VISIT_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_bounty_visits` (
          `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
          `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
          `duration` BIGINT NOT NULL,
          `start_time` TIMESTAMP NOT NULL,
          `ditch_time` TIMESTAMP NOT NULL
        );
      """;

  static final String CREATE_VISIT =
      """
      INSERT INTO `auroramc_bounty_visits`
        (`user_id`, `duration`, `start_time`, `ditch_time`)
      VALUES
        (?, ?, ?, ?);
      """;

  static final String FIND_VISITS_BY_USER_ID =
      """
      SELECT
        `user_id`, `duration`, `start_time`, `ditch_time`
      FROM
        `auroramc_bounty_visits`
      WHERE
        `user_id` = ?
      LIMIT
        20;
      """;

  static final String FIND_VISITS_BY_USER_ID_BETWEEN =
      """
      SELECT
        `user_id`, `duration`, `start_time`, `ditch_time`
      FROM
        `auroramc_bounty_visits`
      WHERE
        `user_id` = ?
      AND
        `start_time` BETWEEN ? AND ?
      LIMIT
        20;
      """;

  private SqlVisitRepositoryQuery() {}
}
