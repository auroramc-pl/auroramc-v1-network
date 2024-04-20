package pl.auroramc.auctions;

import static java.time.Duration.ofSeconds;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.time.Duration;

public class AuctionsConfig extends OkaeriConfig {

  public static final @Exclude String AUCTIONS_CONFIG_FILE_NAME = "config.yml";

  public Long fundsCurrencyId = 1L;

  public int auctionQueueLimit = 3;

  public Duration auctioningPeriod = ofSeconds(30);
}
