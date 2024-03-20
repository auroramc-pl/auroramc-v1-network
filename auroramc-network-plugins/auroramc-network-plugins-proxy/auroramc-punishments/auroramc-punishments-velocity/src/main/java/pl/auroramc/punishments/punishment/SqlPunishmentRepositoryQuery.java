package pl.auroramc.punishments.punishment;

final class SqlPunishmentRepositoryQuery {

  static final String CREATE_PUNISHMENT_SCHEMA_IF_REQUIRED =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_punishments_punishments`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `penalized_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
          `performer_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
          `reason` VARCHAR(255) NOT NULL,
          `period` BIGINT NOT NULL,
          `scope` ENUM('chatting', 'playing', 'warning', 'ejection'),
          `state` ENUM('active', 'expired', 'revoked', 'onetime'),
          `issued_at` TIMESTAMP NOT NULL,
          `expires_at` TIMESTAMP NOT NULL
        );
      """;

  static final String FIND_PUNISHMENT_BY_ID =
      """
      SELECT
        `id`,
        `penalized_id`,
        `performer_id`,
        `reason`,
        `period`,
        `scope`,
        `state`,
        `issued_at`,
        `expires_at`
      FROM
        `auroramc_punishments_punishments`
      WHERE
        `id` = ?;
      """;

  static final String FIND_PUNISHMENT_BY_PENALIZED_ID_WITH_SCOPE_AND_STATE =
      """
      SELECT
        `id`,
        `penalized_id`,
        `performer_id`,
        `reason`,
        `period`,
        `scope`,
        `state`,
        `issued_at`,
        `expires_at`
      FROM
        `auroramc_punishments_punishments`
      WHERE
        `penalized_id` = ?
      AND
        `scope` = ?
      AND
        `state` = ?;
      """;

  static final String FIND_PUNISHMENTS_BY_PENALIZED_ID =
      """
      SELECT
        `id`,
        `penalized_id`,
        `performer_id`,
        `reason`,
        `period`,
        `scope`,
        `state`,
        `issued_at`,
        `expires_at`
      FROM
        `auroramc_punishments_punishments`
      WHERE
        `penalized_id` = ?;
      """;

  static final String CREATE_PUNISHMENT =
      """
      INSERT INTO
        `auroramc_punishments_punishments`
        (`penalized_id`, `performer_id`, `reason`, `period`, `scope`, `state`, `issued_at`, `expires_at`)
      VALUES
        (?, ?, ?, ?, ?, ?, ?, ?);
      """;

  static final String UPDATE_PUNISHMENT =
      """
      UPDATE
        `auroramc_punishments_punishments`
      SET
        `penalized_id` = ?,
        `performer_id` = ?,
        `reason` = ?,
        `period` = ?,
        `scope` = ?,
        `state` = ?,
        `issued_at` = ?,
        `expires_at` = ?
      WHERE
        `id` = ?;
      """;

  private SqlPunishmentRepositoryQuery() {}
}
