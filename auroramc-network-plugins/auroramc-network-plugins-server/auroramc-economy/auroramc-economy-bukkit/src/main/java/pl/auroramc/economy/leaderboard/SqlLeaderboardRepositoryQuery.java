package pl.auroramc.economy.leaderboard;

final class SqlLeaderboardRepositoryQuery {

  static final String GET_LEADERBOARD_ENTRIES_BY_BALANCE_ASCENDING =
      """
      SELECT
        `unique_id`, `username`, `currency_id`, `balance`,
        ROW_NUMBER() OVER (ORDER BY `balance` DESC, `username`) AS `position`
      FROM
        `auroramc_economy_accounts`
      LEFT JOIN `auroramc_registry_users`
        ON `auroramc_registry_users`.`id` = `auroramc_economy_accounts`.`user_id`
      WHERE
        `currency_id` = ?
      ORDER BY
        `balance` DESC, `username`
      LIMIT 10;
      """;

  static final String GET_LEADERBOARD_ENTRY_BY_UNIQUE_ID =
      """
      SELECT
        `unique_id`, `username`, `currency_id`, `balance`,
        (
          SELECT COUNT(*) + 1
          FROM `auroramc_economy_accounts` AS `counter_query`
          LEFT JOIN `auroramc_registry_users` AS `counter_users`
            ON `counter_users`.`id` = `counter_query`.`user_id`
          WHERE
            (
              `counter_query`.`balance` > `primary_query`.`balance`
              OR (
                `counter_query`.`balance` = `primary_query`.`balance`
                AND `counter_users`.`username` > `primary_users`.`username`
              )
            )
            AND `counter_query`.`currency_id` = ?
        ) AS `position`
      FROM
        `auroramc_economy_accounts` AS `primary_query`
      LEFT JOIN `auroramc_registry_users` AS `primary_users`
        ON `primary_users`.`id` = `primary_query`.`user_id`
      WHERE
        `currency_id` = ? AND `unique_id` = ?
      ORDER BY `position`;
      """;

  private SqlLeaderboardRepositoryQuery() {}
}
