package pl.auroramc.bazaars;

import static pl.auroramc.bazaars.BazaarsConfig.BAZAARS_CONFIG_FILE_NAME;
import static pl.auroramc.bazaars.bazaar.BazaarFacade.getBazaarFacade;
import static pl.auroramc.bazaars.bazaar.parser.BazaarParser.getBazaarParser;
import static pl.auroramc.commons.BukkitUtils.registerListeners;
import static pl.auroramc.commons.BukkitUtils.resolveService;

import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import java.util.Optional;
import org.bukkit.plugin.java.JavaPlugin;
import pl.auroramc.bazaars.bazaar.BazaarFacade;
import pl.auroramc.bazaars.bazaar.listener.BazaarCreateListener;
import pl.auroramc.bazaars.bazaar.listener.BazaarUsageListener;
import pl.auroramc.commons.config.ConfigFactory;
import pl.auroramc.commons.config.serdes.SerdesCommons;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.registry.user.UserFacade;

public class BazaarsBukkitPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    final ConfigFactory configFactory = new ConfigFactory(getDataFolder().toPath(), YamlBukkitConfigurer::new);

    final BazaarsConfig bazaarsConfig = configFactory.produceConfig(
        BazaarsConfig.class, BAZAARS_CONFIG_FILE_NAME, new SerdesCommons()
    );

    final UserFacade userFacade = resolveService(getServer(), UserFacade.class);

    final CurrencyFacade currencyFacade = resolveService(getServer(), CurrencyFacade.class);
    final Currency fundsCurrency = Optional.ofNullable(currencyFacade.getCurrencyById(bazaarsConfig.fundsCurrencyId))
        .orElseThrow(() ->
            new BazaarsInitializationException(
                "Could not resolve funds currency, make sure that the currency's id is valid."
            )
        );
    final EconomyFacade economyFacade = resolveService(getServer(), EconomyFacade.class);

    final BazaarFacade bazaarFacade = getBazaarFacade(
        this, bazaarsConfig.priceFormat, fundsCurrency, economyFacade
    );

    registerListeners(this,
        new BazaarCreateListener(bazaarsConfig.priceFormat, getBazaarParser()),
        new BazaarUsageListener(getBazaarParser(), bazaarFacade, userFacade)
    );
  }
}
