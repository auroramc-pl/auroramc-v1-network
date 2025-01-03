package pl.auroramc.essentials;

import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.commons.search.FuzzySearch.getFuzzySearch;
import static pl.auroramc.commons.search.StringMetric.getStringMetric;
import static pl.auroramc.essentials.EssentialsConfig.ESSENTIALS_CONFIG_FILE_NAME;
import static pl.auroramc.essentials.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.search.FuzzySearch;
import pl.auroramc.commons.search.StringMetric;
import pl.auroramc.essentials.command.CommandListener;
import pl.auroramc.essentials.message.MessageSource;
import pl.auroramc.integrations.configs.ConfigFactory;
import pl.auroramc.integrations.configs.serdes.SerdesCommons;
import pl.auroramc.integrations.configs.serdes.message.SerdesMessages;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class EssentialsBukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final EssentialsConfig essentialsConfig =
        configFactory.produceConfig(
            EssentialsConfig.class, ESSENTIALS_CONFIG_FILE_NAME, new SerdesCommons());

    final Scheduler scheduler = getBukkitScheduler(this);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler(scheduler);

    final StringMetric stringMetric =
        getStringMetric(essentialsConfig.prefixScale, essentialsConfig.prefixLength);
    final FuzzySearch fuzzySearch = getFuzzySearch(stringMetric);

    registerListeners(
        this,
        new CommandListener(
            getServer(), scheduler, fuzzySearch, messageSource, messageCompiler, essentialsConfig));
  }
}
