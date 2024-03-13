package pl.auroramc.quests.objective.progress;

final class SqlObjectiveProgressRepositoryQuery {

  static final String CREATE_OBJECTIVE_PROGRESS_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_quests_objective_progresses`
      (
        `user_id` BIGINT REFERENCES `auroramc_registry_users` (`id`) ON DELETE CASCADE,
        `quest_id` BIGINT REFERENCES `auroramc_quests_resource_keys` (`id`) ON DELETE CASCADE,
        `objective_id` BIGINT REFERENCES `auroramc_quests_resource_keys` (`id`) ON DELETE CASCADE,
        `data` INT,
        `goal` INT
      );
      """;

  static final String GET_OBJECTIVE_PROGRESSES =
      """
      SELECT
        `user_id`, `quest_id`, `objective_id`, `data`, `goal`
      FROM
        `auroramc_quests_objective_progresses`
      WHERE
        `user_id` = ? AND `quest_id` = ?;
      """;

  static final String GET_OBJECTIVE_PROGRESS =
      """
      SELECT
        `user_id`, `quest_id`, `objective_id`, `data`, `goal`
      FROM
        `auroramc_quests_objective_progresses`
      WHERE
        `user_id` = ? AND `quest_id` = ? AND `objective_id` = ?;
      """;

  static final String CREATE_OBJECTIVE_PROGRESS =
      """
      INSERT INTO
        `auroramc_quests_objective_progresses`
        (`user_id`, `quest_id`, `objective_id`, `data`, `goal`)
        VALUES
        (?, ?, ?, ?, ?);
      """;

  static final String UPDATE_OBJECTIVE_PROGRESS =
      """
      UPDATE
        `auroramc_quests_objective_progresses`
      SET
        `data` = ?
      WHERE
        `user_id` = ? AND `quest_id` = ? AND `objective_id` = ?;
      """;

  static final String DELETE_OBJECTIVE_PROGRESS_BY_USER_ID_AND_QUEST_ID =
      """
      DELETE FROM
        `auroramc_quests_objective_progresses`
      WHERE
        `user_id` = ? AND `quest_id` = ?;
      """;

  private SqlObjectiveProgressRepositoryQuery() {

  }
}
