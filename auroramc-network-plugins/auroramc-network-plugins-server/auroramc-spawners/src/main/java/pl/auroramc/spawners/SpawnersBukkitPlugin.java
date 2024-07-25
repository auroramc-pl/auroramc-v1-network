package pl.auroramc.spawners;

import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.resource.ResourceUtils.unpackResources;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;
import static pl.auroramc.spawners.SpawnersConfig.SPAWNERS_CONFIG_FILE_NAME;
import static pl.auroramc.spawners.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.spawners.spawner.SpawnerFacade.getSpawnerFacade;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.serdes.SerdesCommons;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.spawners.message.MessageSource;
import pl.auroramc.spawners.spawner.SpawnerBreakListener;
import pl.auroramc.spawners.spawner.SpawnerController;
import pl.auroramc.spawners.spawner.SpawnerFacade;
import pl.auroramc.spawners.spawner.SpawnerInteractionListener;
import pl.auroramc.spawners.spawner.SpawnerPlaceListener;

public class SpawnersBukkitPlugin extends JavaPlugin {

  private static final String GUIS_DIRECTORY_NAME = "guis";
  private static final String SPAWNERS_DIRECTORY_NAME = "spawners";

  @Override
  public void onEnable() {
    unpackResources(
        getFile(), getDataFolder(), Set.of(GUIS_DIRECTORY_NAME, SPAWNERS_DIRECTORY_NAME), Set.of());

    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final SpawnersConfig spawnersConfig =
        configFactory.produceConfig(
            SpawnersConfig.class, SPAWNERS_CONFIG_FILE_NAME, new SerdesCommons());

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = getFundsCurrency(currencyFacade, spawnersConfig.fundsCurrencyId);

    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final SpawnerFacade spawnerFacade =
        getSpawnerFacade(getClassLoader(), getSpawnersDirectoryPath());
    final SpawnerController spawnerController =
        new SpawnerController(
            scheduler, fundsCurrency, economyFacade, messageSource.spawner, messageCompiler);

    final NamespacedKey spawnedCreatureKey = new NamespacedKey(this, "spawner_creature_type");

    registerListeners(
        this,
        new SpawnerInteractionListener(
            this,
            fundsCurrency,
            spawnerFacade,
            spawnerController,
            messageSource.spawner,
            messageCompiler),
        new SpawnerBreakListener(
            spawnedCreatureKey, spawnersConfig, messageSource.spawner, messageCompiler),
        new SpawnerPlaceListener(spawnedCreatureKey));
  }

  private Path getSpawnersDirectoryPath() {
    return getDataFolder().toPath().resolve(SPAWNERS_DIRECTORY_NAME);
  }

  private Currency getFundsCurrency(
      final CurrencyFacade currencyFacade, final long fundsCurrencyId) {
    return Optional.ofNullable(currencyFacade.getCurrencyById(fundsCurrencyId))
        .orElseThrow(
            () ->
                new SpawnersInstantiationException(
                    "Could not resolve funds currency, make sure that the currency's id is valid."));
  }
}
