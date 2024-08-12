package pl.auroramc.registry.provider;

import pl.auroramc.commons.sql.SqlQuery;

final class SqlProviderRepositoryQuery {

  static final @SqlQuery String CREATE_PROVIDER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_providers`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `name` VARCHAR(255) NOT NULL
        );
      """;

  static final @SqlQuery String CREATE_INDEX_ON_PROVIDER_NAME =
      """
      CREATE UNIQUE INDEX IF NOT EXISTS
        `auroramc_registry_providers_index_on_name`
      ON
        `auroramc_registry_providers`
        (`name`);
      """;

  static final @SqlQuery String FIND_PROVIDER_BY_NAME =
      """
      SELECT
        `id`, `name`
      FROM
        `auroramc_registry_providers`
      WHERE
        `name` = ?;
      """;

  static final @SqlQuery String CREATE_PROVIDER =
      """
      INSERT INTO
        `auroramc_registry_providers`
        (`name`)
        VALUES
        (?);
      """;

  static final @SqlQuery String UPDATE_PROVIDER =
      """
      UPDATE
        `auroramc_registry_providers`
      SET
        `name` = ?
      WHERE
        `id` = ?;
      """;

  private SqlProviderRepositoryQuery() {}
}
