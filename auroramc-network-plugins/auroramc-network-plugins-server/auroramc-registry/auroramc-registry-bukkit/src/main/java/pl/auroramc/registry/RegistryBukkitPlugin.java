package pl.auroramc.registry;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerFacades;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerServices;
import static pl.auroramc.integrations.configs.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.registry.observer.ObserverFacadeFactory.getObserverFacade;
import static pl.auroramc.registry.provider.ProviderFacadeFactory.getProviderFacade;
import static pl.auroramc.registry.resource.key.ResourceKeyFacadeFactory.getResourceKeyFacade;
import static pl.auroramc.registry.user.UserFacadeFactory.getUserFacade;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Set;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import pl.auroramc.integrations.IntegrationsBukkitPlugin;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.juliet.JulietConfig;
import pl.auroramc.integrations.configs.serdes.juliet.SerdesJuliet;
import pl.auroramc.registry.observer.ObserverController;
import pl.auroramc.registry.observer.ObserverFacade;
import pl.auroramc.registry.provider.ProviderFacade;
import pl.auroramc.registry.resource.key.ResourceKeyFacade;
import pl.auroramc.registry.user.UserFacade;
import pl.auroramc.registry.user.UserListener;

public class RegistryBukkitPlugin extends IntegrationsBukkitPlugin {

  private Juliet juliet;

  @Override
  public void onStartup() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    final UserFacade userFacade = getUserFacade(getScheduler(), juliet);
    registerListeners(this, new UserListener(userFacade));

    final ProviderFacade providerFacade = getProviderFacade(juliet);
    final ObserverFacade observerFacade = getObserverFacade(getScheduler(), juliet);
    final ObserverController observerController =
        new ObserverController(userFacade, providerFacade, observerFacade);
    registerServices(this, Set.of(observerController));

    final ResourceKeyFacade resourceKeyFacade = getResourceKeyFacade(juliet);
    registerFacades(this, Set.of(providerFacade, observerFacade, resourceKeyFacade, userFacade));
  }

  @Override
  public void onDisable() {
    juliet.close();
  }
}
