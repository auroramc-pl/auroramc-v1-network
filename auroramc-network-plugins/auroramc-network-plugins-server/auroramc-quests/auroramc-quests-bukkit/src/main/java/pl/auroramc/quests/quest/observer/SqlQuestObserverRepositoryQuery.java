package pl.auroramc.quests.quest.observer;

class SqlQuestObserverRepositoryQuery {

  static final String CREATE_QUEST_OBSERVER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_quests_quest_observers`
      (
        `user_id` BIGINT REFERENCES `auroramc_registry_users` (`id`) ON DELETE CASCADE,
        `quest_id` BIGINT REFERENCES `auroramc_registry_resource_keys` (`id`) ON DELETE CASCADE
      );
      """;

  static final String FIND_QUEST_OBSERVER_BY_USER_UNIQUE_ID =
      """
      SELECT
        `user_id`, `quest_id`
      FROM
        `auroramc_quests_quest_observers`
      JOIN
        `auroramc_registry_users`
      ON
        `auroramc_registry_users`.`id` = `auroramc_quests_quest_observers`.`user_id`
      WHERE
        `auroramc_registry_users`.`unique_id` = ?;
      """;

  static final String CREATE_QUEST_OBSERVER =
      """
      INSERT INTO
        `auroramc_quests_quest_observers`
        (`user_id`, `quest_id`)
      VALUES
        (?, ?);
      """;

  static final String UPDATE_QUEST_OBSERVER =
      """
      UPDATE
        `auroramc_quests_quest_observers`
      SET
        `quest_id` = ?
      WHERE
        `user_id` = ?;
      """;

  SqlQuestObserverRepositoryQuery() {}
}
