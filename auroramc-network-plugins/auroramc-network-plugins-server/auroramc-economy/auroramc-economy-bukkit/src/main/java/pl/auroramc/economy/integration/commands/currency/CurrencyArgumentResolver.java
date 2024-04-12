package pl.auroramc.economy.integration.commands.currency;

import static com.pivovarit.function.ThrowingSupplier.lifted;
import static dev.rollczi.litecommands.argument.parser.ParseResult.failure;
import static java.lang.Long.parseLong;
import static pl.auroramc.economy.message.MessageSourcePaths.CURRENCY_ID_PATH;

import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.messages.message.MutableMessage;

public class CurrencyArgumentResolver<T> extends ArgumentResolver<T, Currency> {

  private final CurrencyFacade currencyFacade;
  private final MutableMessage currencyNotFound;

  public CurrencyArgumentResolver(
      final CurrencyFacade currencyFacade, final MutableMessage currencyNotFound) {
    this.currencyFacade = currencyFacade;
    this.currencyNotFound = currencyNotFound;
  }

  @Override
  protected ParseResult<Currency> parse(
      final Invocation<T> invocation,
      final Argument<Currency> argument,
      final String value) {
    return lifted(() -> parseLong(value))
        .get()
        .map(currencyFacade::getCurrencyById)
        .map(ParseResult::success)
        .orElse(failure(currencyNotFound.placeholder(CURRENCY_ID_PATH, value)));
  }
}
