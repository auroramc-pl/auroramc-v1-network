package pl.auroramc.auctions.message.viewer;

final class SqlMessageViewerRepositoryQuery {

  static final String CREATE_MESSAGE_VIEWER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_auctions_message_viewers` (
        `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
        `receive_messages` TINYINT(1) DEFAULT 1
      );
      """;

  static final String FIND_MESSAGE_VIEWER_BY_USER_UNIQUE_ID =
      """
      SELECT
        `user_id`,
        `receive_messages`
      FROM
        `auroramc_auctions_message_viewers`
      LEFT JOIN
        `auroramc_registry_users`
      ON
        `auroramc_registry_users`.`id` = `auroramc_auctions_message_viewers`.`user_id`
      WHERE
        `unique_id` = ?;
      """;

  static final String CREATE_MESSAGE_VIEWER =
      """
      INSERT INTO
        `auroramc_auctions_message_viewers`
        (`user_id`, `receive_messages`)
      VALUES
        (?, ?);
      """;

  static final String UPDATE_MESSAGE_VIEWER =
      """
      UPDATE
        `auroramc_auctions_message_viewers`
      SET
        `receive_messages` = ?
      WHERE
        `user_id` = ?;
      """;

  private SqlMessageViewerRepositoryQuery() {

  }
}
