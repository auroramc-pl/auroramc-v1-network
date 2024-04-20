package pl.auroramc.auctions.vault.item;

final class SqlVaultItemRepositoryQuery {

  static final String CREATE_VAULT_ITEM_SCHEMA =
      """
      CREATE TABLE IF NOT EXISTS `auroramc_auctions_vault_items`
      (
        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
        `user_id` BIGINT REFERENCES `auroramc_registry_users`(`id`),
        `vault_id` BIGINT REFERENCES `auroramc_auctions_vaults`(`id`),
        `subject` BLOB
      );
      """;

  static final String FIND_VAULT_ITEMS_BY_USER_ID =
      """
      SELECT
        `auroramc_auctions_vault_items`.`id`,
        `auroramc_auctions_vault_items`.`user_id`,
        `auroramc_auctions_vault_items`.`vault_id`,
        `auroramc_auctions_vault_items`.`subject`
      FROM
        `auroramc_auctions_vault_items`
      LEFT JOIN
        `auroramc_auctions_vaults`
        ON
          `auroramc_auctions_vaults`.`id` = `auroramc_auctions_vault_items`.`vault_id`
      LEFT JOIN
        `auroramc_registry_users`
        ON `auroramc_auctions_vaults`.`user_id` = `auroramc_registry_users`.`id`
      WHERE
        `auroramc_registry_users`.`id` = ?;
      """;

  static final String CREATE_VAULT_ITEM =
      """
      INSERT INTO
        `auroramc_auctions_vault_items`
        (`user_id`, `vault_id`, `subject`)
      VALUES
        (?, ?, ?);
      """;

  static final String DELETE_VAULT_ITEM =
      """
      DELETE FROM
        `auroramc_auctions_vault_items`
      WHERE
        `id` = ?;
      """;

  private SqlVaultItemRepositoryQuery() {}
}
