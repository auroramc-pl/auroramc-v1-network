package pl.auroramc.bounties;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.time.Duration;

public class BountyConfig extends OkaeriConfig {

  public static final @Exclude String BOUNTY_CONFIG_FILE_NAME = "config.yml";

  public Duration visitBuffer = ofSeconds(30);

  public Duration bountyBuffer = ofMinutes(30);
}
