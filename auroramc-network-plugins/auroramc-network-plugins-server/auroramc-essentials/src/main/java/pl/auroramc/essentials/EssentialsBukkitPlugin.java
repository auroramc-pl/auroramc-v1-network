package pl.auroramc.essentials;

import static moe.rafal.juliet.datasource.hikari.HikariPooledDataSourceFactory.getHikariDataSource;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.config.juliet.JulietConfig.JULIET_CONFIG_FILE_NAME;
import static pl.auroramc.commons.search.FuzzySearch.getFuzzySearch;
import static pl.auroramc.commons.search.StringMetric.getStringMetric;
import static pl.auroramc.essentials.EssentialsConfig.ESSENTIALS_CONFIG_FILE_NAME;
import static pl.auroramc.essentials.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import moe.rafal.juliet.Juliet;
import moe.rafal.juliet.JulietBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.juliet.JulietConfig;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.commons.config.serdes.juliet.SerdesJuliet;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.search.FuzzySearch;
import pl.auroramc.commons.search.StringMetric;
import pl.auroramc.essentials.command.CommandListener;
import pl.auroramc.essentials.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class EssentialsBukkitPlugin extends JavaPlugin {

  private Juliet juliet;

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final EssentialsConfig essentialsConfig =
        configFactory.produceConfig(
            EssentialsConfig.class, ESSENTIALS_CONFIG_FILE_NAME, new SerdesCommons());

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler();

    final StringMetric stringMetric =
        getStringMetric(essentialsConfig.prefixScale, essentialsConfig.prefixLength);
    final FuzzySearch fuzzySearch = getFuzzySearch(stringMetric);

    final JulietConfig julietConfig =
        configFactory.produceConfig(
            JulietConfig.class, JULIET_CONFIG_FILE_NAME, new SerdesJuliet());
    juliet =
        JulietBuilder.newBuilder().withDataSource(getHikariDataSource(julietConfig.hikari)).build();

    registerListeners(
        this,
        new CommandListener(
            getServer(), fuzzySearch, messageSource, messageCompiler, essentialsConfig));
  }

  @Override
  public void onDisable() {
    juliet.close();
  }
}
