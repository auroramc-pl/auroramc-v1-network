package pl.auroramc.economy.rest.server;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class RestSecurityConfig extends OkaeriConfig {

  public String apiKeyHeaderName = "X-API-Key";

  @Comment("Firstly, you should generate a new api key using a strong password generator, then keep it safe and do not share it with anyone.")
  public String apiKey = "1e2aUbE8ZD0kzt6Pic7rmkbeYThIQlYk";
}
