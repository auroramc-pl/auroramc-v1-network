package pl.auroramc.bounties.progress;

final class SqlBountyProgressRepositoryQuery {

  static final String CREATE_BOUNTY_PROGRESS_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_bounties_bounty_progresses`
      (
        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
        `user_id` BIGINT REFERENCES `auroramc_registry_users` (`id`) ON DELETE CASCADE,
        `day` BIGINT DEFAULT 0,
        `acquisition_date` DATE NULL
      );
      """;

  static final String FIND_BOUNTY_PROGRESS_BY_USER_ID =
      """
      SELECT
        `id`, `user_id`, `day`, `acquisition_date`
      FROM
        `auroramc_bounties_bounty_progresses`
      WHERE
        `user_id` = ?;
      """;

  static final String CREATE_BOUNTY_PROGRESS =
      """
      INSERT INTO
        `auroramc_bounties_bounty_progresses`
        (`user_id`, `day`, `acquisition_date`)
        VALUES
        (?, ?, ?);
      """;

  static final String UPDATE_BOUNTY_PROGRESS =
      """
      UPDATE
        `auroramc_bounties_bounty_progresses`
      SET
        `day` = ?, `acquisition_date` = ?
      WHERE
        `id` = ?;
      """;

  private SqlBountyProgressRepositoryQuery() {}
}
