package pl.auroramc.economy.rest.server;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class RestServerConfig extends OkaeriConfig {

  @Comment(
      "This feature is intended for development purposes only, thought it could be used in production as well, but it would be recommended to access it through private network.")
  public boolean enabled = false;

  public int port = 8080;

  public RestSecurityConfig security = new RestSecurityConfig();
}
