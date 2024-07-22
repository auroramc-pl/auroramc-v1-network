package pl.auroramc.quests.quest.track;

final class SqlQuestTrackRepositoryQuery {

  static final String CREATE_QUEST_TRACK_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_quests_quest_tracks`
      (
        `user_id` BIGINT REFERENCES `auroramc_registry_users` (`id`) ON DELETE CASCADE,
        `quest_id` BIGINT REFERENCES `auroramc_registry_resource_keys` (`id`) ON DELETE CASCADE,
        `quest_state` ENUM ('IN_PROGRESS', 'COMPLETED') DEFAULT 'IN_PROGRESS'
      );
      """;

  static final String GET_QUEST_TRACKS_BY_USER_UNIQUE_ID =
      """
      SELECT
        `user_id`, `quest_id`, `quest_state`
      FROM
        `auroramc_quests_quest_tracks`
      LEFT JOIN
        `auroramc_registry_users`
      ON
        `auroramc_registry_users`.`id` = `auroramc_quests_quest_tracks`.`user_id`
      WHERE
        `auroramc_registry_users`.`unique_id` = ?;
      """;

  static final String CREATE_QUEST_TRACK =
      """
      INSERT INTO
        `auroramc_quests_quest_tracks`
        (`user_id`, `quest_id`, `quest_state`)
      VALUES
        (?, ?, ?);
      """;

  static final String UPDATE_QUEST_TRACK =
      """
      UPDATE
        `auroramc_quests_quest_tracks`
      SET
        `quest_state` = ?
      WHERE
        `user_id` = ? AND `quest_id` = ?;
      """;

  private SqlQuestTrackRepositoryQuery() {}
}
