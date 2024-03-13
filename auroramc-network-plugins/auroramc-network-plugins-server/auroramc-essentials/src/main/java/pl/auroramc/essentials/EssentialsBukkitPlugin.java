package pl.auroramc.essentials;

import static pl.auroramc.essentials.EssentialsConfig.PLUGIN_CONFIG_FILE_NAME;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.search.FuzzySearch.getJaroWinklerSearchWithDefaultSettings;
import static pl.auroramc.essentials.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.essentials.command.CommandListener;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessageSource;
import pl.auroramc.commons.search.FuzzySearch;
import pl.auroramc.essentials.message.MessageSource;

public class EssentialsBukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final EssentialsConfig essentialsConfig = configFactory.produceConfig(
        EssentialsConfig.class, PLUGIN_CONFIG_FILE_NAME
    );
    final MessageSource messageSource = configFactory.produceConfig(
        MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessageSource()
    );

    final FuzzySearch fuzzySearch = getJaroWinklerSearchWithDefaultSettings();

    registerListeners(this,
        new CommandListener(getServer(), fuzzySearch, messageSource, essentialsConfig)
    );
  }
}
