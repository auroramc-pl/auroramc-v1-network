package pl.auroramc.registry;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerFacades;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.registry.resource.key.ResourceKeyFacadeFactory.getResourceKeyFacade;
import static pl.auroramc.registry.resource.provider.ResourceProviderFacadeFactory.getResourceProviderFacade;
import static pl.auroramc.registry.user.UserFacadeFactory.getUserFacade;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Set;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.registry.resource.key.ResourceKeyFacade;
import pl.auroramc.registry.resource.provider.ResourceProviderFacade;
import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.registry.user.UserListener;

public class RegistryBukkitPlugin extends JavaPlugin {

  private Juliet juliet;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final Scheduler scheduler = getBukkitScheduler(this);

    final ResourceProviderFacade resourceProviderFacade = getResourceProviderFacade(juliet);
    final ResourceKeyFacade resourceKeyFacade = getResourceKeyFacade(juliet);
    final UserFacade userFacade = getUserFacade(scheduler, juliet);
    registerListeners(this, new UserListener(userFacade));
    registerFacades(this, Set.of(resourceProviderFacade, resourceKeyFacade, userFacade));
  }

  @Override
  public void onDisable() {
    juliet.close();
  }
}
