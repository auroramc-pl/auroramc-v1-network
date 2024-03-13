package pl.auroramc.quests.resource.key;

final class SqlResourceKeyRepositoryQuery {

  static final String CREATE_QUEST_KEY_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_quests_resource_keys`
      (
        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
        `name` VARCHAR(64) NOT NULL,
        UNIQUE (`name`)
      );
      """;

  static final String GET_RESOURCE_KEYS =
      """
      SELECT
        `id`, `name`
      FROM
        `auroramc_quests_resource_keys`;
      """;

  static final String CREATE_RESOURCE_KEY =
      """
      INSERT INTO
        `auroramc_quests_resource_keys` (`name`)
      VALUES
        (?);
      """;

  static final String DELETE_RESOURCE_KEY =
      """
      DELETE FROM
        `auroramc_quests_resource_keys`
      WHERE
        `id` = ?;
      """;

  private SqlResourceKeyRepositoryQuery() {

  }
}
