package pl.auroramc.commons.config.serdes.juliet;

import com.zaxxer.hikari.HikariConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;

public class JulietConfig extends OkaeriConfig {

  public static final @Exclude String JULIET_CONFIG_FILE_NAME = "juliet.yml";

  public HikariConfig hikari = getDefaultHikariConfig();

  private HikariConfig getDefaultHikariConfig() {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/auroramc_economy");
    hikariConfig.setUsername("auroramc_identity");
    hikariConfig.setPassword("my-secret-password-123-!@#");
    return hikariConfig;
  }
}