package pl.auroramc.essentials;

import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.search.FuzzySearch.getFuzzySearch;
import static pl.auroramc.commons.search.StringMetric.getStringMetric;
import static pl.auroramc.essentials.EssentialsConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.essentials.message.MutableMessageSource.MESSAGE_SOURCE_FILE_NAME;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.search.FuzzySearch;
import pl.auroramc.commons.search.StringMetric;
import pl.auroramc.essentials.command.CommandListener;
import pl.auroramc.essentials.message.MutableMessageSource;

public class EssentialsBukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final EssentialsConfig essentialsConfig = configFactory.produceConfig(
        EssentialsConfig.class, PLUGIN_CONFIG_FILE_NAME
    );
    final MutableMessageSource messageSource = configFactory.produceConfig(
        MutableMessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final StringMetric stringMetric = getStringMetric(
        essentialsConfig.prefixScale,
        essentialsConfig.prefixLength
    );
    final FuzzySearch fuzzySearch = getFuzzySearch(stringMetric);

    registerListeners(this,
        new CommandListener(getServer(), fuzzySearch, messageSource, essentialsConfig)
    );
  }
}
