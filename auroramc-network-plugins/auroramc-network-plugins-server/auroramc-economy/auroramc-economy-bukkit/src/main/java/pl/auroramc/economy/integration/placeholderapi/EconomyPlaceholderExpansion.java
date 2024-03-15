package pl.auroramc.economy.integration.placeholderapi;

import static java.lang.Long.parseLong;
import static java.lang.String.join;
import static java.util.Arrays.copyOfRange;
import static panda.std.Option.supplyThrowing;

import java.util.logging.Logger;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.auroramc.commons.decimal.DecimalFormatter;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;

class EconomyPlaceholderExpansion extends PlaceholderExpansion {

  private static final Long DEFAULT_CURRENCY_ID = 1L;
  private static final String SYMBOL_PLACEHOLDER_NAME = "symbol";
  private static final String BALANCE_PLACEHOLDER_NAME = "balance";
  private static final String PLACEHOLDER_AUTHORS_DELIMITER = ", ";
  private static final String PLACEHOLDER_PARAMETER_DELIMITER = "_";
  private final Plugin plugin;
  private final Logger logger;
  private final EconomyFacade economyFacade;
  private final CurrencyFacade currencyFacade;

  EconomyPlaceholderExpansion(
      final Plugin plugin,
      final Logger logger,
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade
  ) {
    this.plugin = plugin;
    this.logger = logger;
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  @Override
  public @Nullable String onPlaceholderRequest(
      final Player requester, final @NotNull String params) {
    if (requester == null) {
      return null;
    }

    if (params.startsWith(BALANCE_PLACEHOLDER_NAME) || params.startsWith(SYMBOL_PLACEHOLDER_NAME)) {
      final String[] variablesIncludingName = params.split(PLACEHOLDER_PARAMETER_DELIMITER);
      final String[] variables = copyOfRange(
          variablesIncludingName, 1, variablesIncludingName.length);
      if (variables.length == 0) {
        logger.warning(
            "Could not retrieve any variables from the balance retrieval."
        );
        return null;
      }

      final Long currencyId = supplyThrowing(() -> parseLong(variables[0])).orElseGet(DEFAULT_CURRENCY_ID);
      final Currency currency = currencyFacade.getCurrencyById(currencyId);
      if (currency == null) {
        logger.warning(
            "Could not find currency with id %s to process the balance retrieval."
                .formatted(
                    currencyId
                )
        );
        return null;
      }

      if (params.startsWith(SYMBOL_PLACEHOLDER_NAME)) {
        return currency.getSymbol();
      }

      if (params.startsWith(BALANCE_PLACEHOLDER_NAME)) {
        return economyFacade.balance(requester.getUniqueId(), currency)
            .thenApply(DecimalFormatter::getFormattedDecimal)
            .join();
      }
    }

    return null;
  }

  @Override
  public @NotNull String getIdentifier() {
    return plugin.getName();
  }

  @Override
  public @NotNull String getAuthor() {
    return join(PLACEHOLDER_AUTHORS_DELIMITER,  plugin.getPluginMeta().getAuthors());
  }

  @Override
  public @NotNull String getVersion() {
    return plugin.getPluginMeta().getVersion();
  }
}
