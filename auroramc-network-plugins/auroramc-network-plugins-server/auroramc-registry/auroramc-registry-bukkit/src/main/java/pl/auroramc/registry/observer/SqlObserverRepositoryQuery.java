package pl.auroramc.registry.observer;

final class SqlObserverRepositoryQuery {

  static final String CREATE_OBSERVER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_observers`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `provider_id` BIGINT NOT NULL REFERENCES `auroramc_registry_providers` (`id`) ON DELETE CASCADE,
          `user_id` BIGINT NOT NULL REFERENCES `auroramc_registry_users` (`id`) ON DELETE CASCADE,
          `enabled` BOOLEAN NOT NULL DEFAULT TRUE
        );
      """;

  static final String FIND_OBSERVER_BY_PROVIDER_ID_AND_USER_ID =
      """
      SELECT
        `id`, `provider_id`, `user_id`, `enabled`
      FROM
        `auroramc_registry_observers`
      WHERE
        `provider_id` = ? AND `user_id` = ?;
      """;

  static final String CREATE_OBSERVER =
      """
      INSERT INTO
        `auroramc_registry_observers`
        (`provider_id`, `user_id`, `enabled`)
      VALUES
        (?, ?, ?);
      """;

  static final String UPDATE_OBSERVER =
      """
      UPDATE
        `auroramc_registry_observers`
      SET
        `enabled` = ?
      WHERE
        `provider_id` = ? AND `user_id` = ?;
      """;

  private SqlObserverRepositoryQuery() {}
}
