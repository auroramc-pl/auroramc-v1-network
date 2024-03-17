package pl.auroramc.auctions.audience;

final class SqlAudienceRepositoryQuery {

  static final String CREATE_MESSAGE_VIEWER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_auctions_audiences` (
        `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
        `allows_messages` BOOLEAN DEFAULT TRUE
      );
      """;

  static final String FIND_MESSAGE_VIEWER_BY_USER_UNIQUE_ID =
      """
      SELECT
        `user_id`,
        `allows_messages`
      FROM
        `auroramc_auctions_audiences`
      LEFT JOIN
        `auroramc_registry_users`
      ON
        `auroramc_registry_users`.`id` = `auroramc_auctions_audiences`.`user_id`
      WHERE
        `unique_id` = ?;
      """;

  static final String CREATE_MESSAGE_VIEWER =
      """
      INSERT INTO
        `auroramc_auctions_audiences`
        (`user_id`, `allows_messages`)
      VALUES
        (?, ?);
      """;

  static final String UPDATE_MESSAGE_VIEWER =
      """
      UPDATE
        `auroramc_auctions_audiences`
      SET
        `allows_messages` = ?
      WHERE
        `user_id` = ?;
      """;

  private SqlAudienceRepositoryQuery() {

  }
}
