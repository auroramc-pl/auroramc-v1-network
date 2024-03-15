package pl.auroramc.economy;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.economy.balance.BalanceConfig;
import pl.auroramc.economy.balance.leaderboad.LeaderboardConfig;
import pl.auroramc.economy.payment.PaymentConfig;
import pl.auroramc.economy.rest.server.RestServerConfig;
import pl.auroramc.economy.transfer.TransferConfig;

public class EconomyConfig extends OkaeriConfig {

  public static final @Exclude String ECONOMY_CONFIG_FILE_NAME = "config.yml";

  public BalanceConfig balance = new BalanceConfig();

  public PaymentConfig payment = new PaymentConfig();

  public TransferConfig transfer = new TransferConfig();

  public RestServerConfig restServer = new RestServerConfig();

  public LeaderboardConfig leaderboard = new LeaderboardConfig();
}
