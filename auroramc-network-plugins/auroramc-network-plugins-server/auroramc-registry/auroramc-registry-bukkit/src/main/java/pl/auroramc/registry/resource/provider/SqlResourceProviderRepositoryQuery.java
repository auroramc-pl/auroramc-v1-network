package pl.auroramc.registry.resource.provider;

final class SqlResourceProviderRepositoryQuery {

  static final String CREATE_RESOURCE_PROVIDER_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_resource_providers`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `name` VARCHAR(255) NOT NULL
        );
      """;

  static final String CREATE_INDEX_ON_RESOURCE_PROVIDER_NAME =
      """
      CREATE UNIQUE INDEX IF NOT EXISTS
        `auroramc_registry_resource_providers_index_on_name`
      ON
        `auroramc_registry_resource_providers`
        (`name`);
      """;

  static final String FIND_RESOURCE_PROVIDER_BY_NAME =
      """
      SELECT
        `id`, `name`
      FROM
        `auroramc_registry_resource_providers`
      WHERE
        `name` = ?;
      """;

  static final String CREATE_RESOURCE_PROVIDER =
      """
      INSERT INTO
        `auroramc_registry_resource_providers`
        (`name`)
        VALUES
        (?);
      """;

  static final String UPDATE_RESOURCE_PROVIDER =
      """
      UPDATE
        `auroramc_registry_resource_providers`
      SET
        `name` = ?
      WHERE
        `id` = ?;
      """;

  private SqlResourceProviderRepositoryQuery() {}
}
