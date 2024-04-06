package pl.auroramc.bazaars;

import static pl.auroramc.bazaars.BazaarsConfig.BAZAARS_CONFIG_FILE_NAME;
import static pl.auroramc.bazaars.bazaar.BazaarFacade.getBazaarFacade;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParser.getBazaarParser;
import static pl.auroramc.bazaars.message.MessageSource.MESSAGE_SOURCE_FILE_NAME;
import static pl.auroramc.commons.bukkit.BukkitUtils.registerListeners;
import static pl.auroramc.commons.bukkit.BukkitUtils.resolveService;
import static pl.auroramc.commons.bukkit.scheduler.BukkitSchedulerFactory.getBukkitScheduler;
import static pl.auroramc.messages.message.compiler.BukkitMessageCompiler.getBukkitMessageCompiler;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.bazaars.bazaar.BazaarFacade;
import pl.auroramc.bazaars.bazaar.listener.BazaarCreateListener;
import pl.auroramc.bazaars.bazaar.listener.BazaarUsageListener;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.message.SerdesMessages;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.registry.user.UserFacade;

public class BazaarsBukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    final ConfigFactory configFactory =
        new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);
    final BazaarsConfig bazaarsConfig =
        configFactory.produceConfig(BazaarsConfig.class, BAZAARS_CONFIG_FILE_NAME);

    final MessageSource messageSource =
        configFactory.produceConfig(
            MessageSource.class, MESSAGE_SOURCE_FILE_NAME, new SerdesMessages());
    final BukkitMessageCompiler messageCompiler = getBukkitMessageCompiler();

    final Scheduler scheduler = getBukkitScheduler(this);

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = getFundsCurrency(currencyFacade, bazaarsConfig.fundsCurrencyId);
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final BazaarFacade bazaarFacade =
        getBazaarFacade(scheduler, messageSource, economyFacade, fundsCurrency);

    registerListeners(
        this,
        new BazaarCreateListener(messageSource, messageCompiler, getBazaarParser()),
        new BazaarUsageListener(
            messageSource, messageCompiler, getBazaarParser(), bazaarFacade, userFacade));
  }

  private Currency getFundsCurrency(
      final CurrencyFacade currencyFacade, final long fundsCurrencyId) {
    return Optional.ofNullable(currencyFacade.getCurrencyById(fundsCurrencyId))
        .orElseThrow(
            () ->
                new BazaarsInstantiationException(
                    "Could not resolve funds currency, make sure that the currency's id is valid."));
  }
}
