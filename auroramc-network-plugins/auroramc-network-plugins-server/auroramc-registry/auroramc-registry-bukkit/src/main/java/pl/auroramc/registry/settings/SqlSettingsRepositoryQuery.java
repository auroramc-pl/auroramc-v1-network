package pl.auroramc.registry.settings;

import pl.auroramc.commons.sql.SqlQuery;

final class SqlSettingsRepositoryQuery {

  static final @SqlQuery String CREATE_SETTINGS_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_settings`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
          `language_id` BIGINT REFERENCES `auroramc_registry_languages`(`id`)
        );
      """;

  static final @SqlQuery String CREATE_LANGUAGES_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_registry_languages`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `tag` VARCHAR(2) UNIQUE NOT NULL
        );
      """;

  static final @SqlQuery String FIND_SETTINGS_BY_USER_ID =
      """
      SELECT
        `auroramc_registry_settings`.`id`,
        `auroramc_registry_settings`.`user_id`,
        `auroramc_registry_languages`.`tag`
      FROM
        `auroramc_registry_settings`
      JOIN
        `auroramc_registry_users`
      ON
        `auroramc_registry_settings`.`user_id` = `auroramc_registry_users`.`id`
      JOIN
        `auroramc_registry_languages`
      ON
        `auroramc_registry_settings`.`language_id` = `auroramc_registry_languages`.`id`
      WHERE
        `auroramc_registry_settings`.`user_id` = ?;
      """;

  static final @SqlQuery String CREATE_SETTINGS =
      """
      INSERT INTO
        `auroramc_registry_settings`
        (`user_id`, `language_id`)
      VALUES
        (
          ?,
          (SELECT `auroramc_registry_languages`.`id` FROM `auroramc_registry_languages` WHERE `auroramc_registry_languages`.`tag` = ?)
        );
      """;

  static final @SqlQuery String UPDATE_SETTINGS =
      """
      UPDATE
        `auroramc_registry_settings`
      JOIN
        `auroramc_registry_languages`
      ON
        `auroramc_registry_languages`.`tag` = ?
      SET
        `auroramc_registry_settings`.`language_id` = `auroramc_registry_languages`.`id`
      WHERE
        `auroramc_registry_settings`.`id` = ?;
      """;

  private SqlSettingsRepositoryQuery() {}
}
