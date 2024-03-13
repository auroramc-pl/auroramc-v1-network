package pl.auroramc.economy.balance;

import static io.javalin.http.HttpStatus.BAD_REQUEST;
import static io.javalin.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static io.javalin.http.HttpStatus.OK;
import static panda.std.Option.supplyThrowing;
import static pl.auroramc.economy.rest.server.RestResponse.response;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.UUID;
import panda.std.Option;
import pl.auroramc.commons.decimal.DecimalFormatter;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;

public class BalanceRequestHandler implements Handler {

  private final EconomyFacade economyFacade;
  private final CurrencyFacade currencyFacade;

  public BalanceRequestHandler(
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade) {
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  @Override
  public void handle(final Context context) {
    final String currencyId = context.pathParam("currencyId");
    final Option<Currency> currencyById = supplyThrowing(() -> Long.parseLong(currencyId))
        .map(currencyFacade::getCurrencyById);
    if (currencyById.isEmpty()) {
      context
          .status(BAD_REQUEST)
          .json(response(BAD_REQUEST, "Could not parse id, or find currency with id %s.".formatted(currencyId)));
      return;
    }

    final String uniqueId = context.pathParam("uniqueId");
    final Option<UUID> parsedUniqueId = supplyThrowing(
        () -> UUID.fromString(context.pathParam("uniqueId")));
    if (parsedUniqueId.isEmpty()) {
      context
          .status(BAD_REQUEST)
          .json(response(BAD_REQUEST, "Could not parse unique id from %s.".formatted(uniqueId)));
      return;
    }

    context.future(() -> economyFacade.balance(parsedUniqueId.get(), currencyById.get())
        .thenApply(DecimalFormatter::getFormattedDecimal)
        .thenApply(balance -> response(OK, balance))
        .thenAccept(context::json)
        .exceptionally(exception -> {
          context
              .status(INTERNAL_SERVER_ERROR)
              .json(response(INTERNAL_SERVER_ERROR, "Could not retrieve balance for %s and currency with id %s, because of unexpected exception.".formatted(uniqueId, currencyId)));
          return null;
        }));
  }
}
