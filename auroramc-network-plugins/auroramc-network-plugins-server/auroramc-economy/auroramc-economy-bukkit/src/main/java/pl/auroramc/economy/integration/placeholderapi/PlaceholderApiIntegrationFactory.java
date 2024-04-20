package pl.auroramc.economy.integration.placeholderapi;

import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.bukkit.integration.ExternalIntegration;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.economy.currency.CurrencyFacade;

public final class PlaceholderApiIntegrationFactory {

  private PlaceholderApiIntegrationFactory() {}

  public static ExternalIntegration getPlaceholderApiIntegration(
      final Plugin plugin, final EconomyFacade economyFacade, final CurrencyFacade currencyFacade) {
    return new PlaceholderApiIntegration(plugin, plugin.getLogger(), economyFacade, currencyFacade);
  }
}
