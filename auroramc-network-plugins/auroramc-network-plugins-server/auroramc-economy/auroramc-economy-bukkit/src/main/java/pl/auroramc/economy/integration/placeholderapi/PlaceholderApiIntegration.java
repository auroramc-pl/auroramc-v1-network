package pl.auroramc.economy.integration.placeholderapi;

import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.bukkit.integration.ExternalIntegrationDelegate;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;

class PlaceholderApiIntegration extends ExternalIntegrationDelegate {

  private static final String PLACEHOLDER_API_PLUGIN_NAME = "PlaceholderAPI";
  private final Plugin plugin;
  private final Logger logger;
  private final EconomyFacade economyFacade;
  private final CurrencyFacade currencyFacade;

  PlaceholderApiIntegration(
      final Plugin plugin,
      final Logger logger,
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade) {
    super(PLACEHOLDER_API_PLUGIN_NAME);
    this.plugin = plugin;
    this.logger = logger;
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  @Override
  public void configure() {
    new EconomyPlaceholderExpansion(plugin, logger, economyFacade, currencyFacade).register();
  }
}
