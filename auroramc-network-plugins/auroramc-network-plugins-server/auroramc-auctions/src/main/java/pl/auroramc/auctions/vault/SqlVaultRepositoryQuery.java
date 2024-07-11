package pl.auroramc.auctions.vault;

final class SqlVaultRepositoryQuery {

  static final String CREATE_VAULT_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS
        `auroramc_auctions_vaults`
        (
          `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
          `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`)
        );
      """;

  static final String FIND_VAULT_BY_USER_ID =
      """
      SELECT
        `id`, `user_id`
      FROM
        `auroramc_auctions_vaults`
      WHERE
        `user_id` = ?;
      """;

  static final String CREATE_VAULT =
      """
      INSERT INTO
        `auroramc_auctions_vaults`
        (`user_id`)
      VALUES
        (?);
      """;

  static final String DELETE_VAULT =
      """
      DELETE FROM
        `auroramc_auctions_vaults`
      WHERE
        `id` = ?;
      """;

  private SqlVaultRepositoryQuery() {}
}
