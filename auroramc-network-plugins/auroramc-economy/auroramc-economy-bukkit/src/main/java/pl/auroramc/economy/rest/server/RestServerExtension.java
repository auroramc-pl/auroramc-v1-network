package pl.auroramc.economy.rest.server;

import static io.javalin.Javalin.create;

import io.javalin.Javalin;
import pl.auroramc.economy.EconomyBukkitPluginConfig;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.balance.BalanceRequestHandler;
import pl.auroramc.economy.currency.CurrencyFacade;

public class RestServerExtension {

  private final CurrencyFacade currencyFacade;
  private final EconomyFacade economyFacade;
  private final EconomyBukkitPluginConfig pluginConfig;
  private Javalin javalin;

  public RestServerExtension(
      final EconomyBukkitPluginConfig pluginConfig,
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade
  ) {
    this.pluginConfig = pluginConfig;
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  private void enableRestServer() {
    javalin = create(configuration -> configuration.showJavalinBanner = false)
        .start(pluginConfig.restServer.port);

    final RestSecurityHandler restSecurityHandler = new RestSecurityHandler(
        pluginConfig.restServer.security);
    javalin.before(restSecurityHandler);
    javalin.exception(RestSecurityException.class, restSecurityHandler);

    javalin.get("/balance/{currencyId}/{uniqueId}",
        new BalanceRequestHandler(economyFacade, currencyFacade));
  }

  public void enableRestServerIfConfigured() {
    if (pluginConfig.restServer.enabled) {
      enableRestServer();
    }
  }

  public void disableRestServerIfRunning() {
    if (javalin != null) {
      javalin.stop();
    }
  }
}
