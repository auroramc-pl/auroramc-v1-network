package pl.auroramc.registry.resource.key;

import pl.auroramc.commons.sql.SqlQuery;

final class SqlResourceKeyRepositoryQuery {

  static final @SqlQuery String CREATE_RESOURCE_KEYS_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_resource_keys`
      (
        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
        `provider_id` BIGINT REFERENCES `auroramc_registry_providers` (`id`) ON DELETE CASCADE,
        `name` VARCHAR(64) NOT NULL,
        UNIQUE (`name`)
      );
      """;

  static final @SqlQuery String GET_RESOURCE_KEYS_BY_PROVIDER_ID =
      """
      SELECT
        `id`, `provider_id`, `name`
      FROM
        `auroramc_registry_resource_keys`
      WHERE
        `provider_id` = ?;
      """;

  static final @SqlQuery String CREATE_RESOURCE_KEY =
      """
      INSERT INTO
        `auroramc_registry_resource_keys` (`provider_id`, `name`)
      VALUES
        (?, ?);
      """;

  static final @SqlQuery String DELETE_RESOURCE_KEY =
      """
      DELETE FROM
        `auroramc_registry_resource_keys`
      WHERE
        `id` = ?;
      """;

  private SqlResourceKeyRepositoryQuery() {}
}
