package pl.auroramc.punishments;

import static moe.rafal.juliet.datasource.HikariPooledDataSourceFactory.produceHikariDataSource;
import static pl.auroramc.commons.config.serdes.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.punishments.BuildManifest.PROJECT_ARTIFACT_ID;
import static pl.auroramc.punishments.BuildManifest.PROJECT_VERSION;
import static pl.auroramc.punishments.punishment.PunishmentFacadeFactory.getPunishmentFacade;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import java.nio.file.Path;
import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.punishments.message.MutableMessageSource;
import pl.auroramc.punishments.punishment.PunishmentFacade;

@Plugin(id = PROJECT_ARTIFACT_ID, version = PROJECT_VERSION, authors = "shitzuu <hello@rafal.moe>")
public class PunishmentsVelocityPlugin {

  private final Logger logger;
  private final ConfigFactory configFactory;

  @Inject
  public PunishmentsVelocityPlugin(final Logger logger, final @DataDirectory Path dataPath) {
    this.logger = logger;
    this.configFactory = new ConfigFactory(dataPath, YamlSnakeYamlConfigurer::new);
  }

  @Subscribe
  public void onProxyInitialize(final ProxyInitializeEvent event) {
    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MutableMessageSource.MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final JulietConfig julietConfig = configFactory.produceConfig(
        JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet()
    );
    final Juliet juliet = JulietBuilder.newBuilder()
        .withDataSource(produceHikariDataSource(julietConfig.hikari))
        .build();

    final PunishmentFacade punishmentFacade = getPunishmentFacade(logger, juliet);
  }
}
