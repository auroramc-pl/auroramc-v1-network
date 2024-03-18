package pl.auroramc.economy;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.economy.message.MutableMessageVariableKey.AMOUNT_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_ID_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.USERNAME_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MutableMessageSource;

@Permission("auroramc.economy.economy")
@Command(name = "economy", aliases = "eco")
public class EconomyCommand {

  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final CurrencyFacade currencyFacade;

  public EconomyCommand(
      final Logger logger,
      final MutableMessageSource messageSource,
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  @Execute(name = "set")
  public CompletableFuture<MutableMessage> set(
      final @Arg Player target,
      final @Arg Long currencyId,
      final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> balance(target, currency, fixedAmount),
        false
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Execute(name = "add")
  public CompletableFuture<MutableMessage> add(
      final @Arg Player target,
      final @Arg Long currencyId,
      final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> deposit(target, currency, fixedAmount),
        true
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Execute(name = "take")
  public CompletableFuture<MutableMessage> take(
      final @Arg Player target,
      final @Arg Long currencyId,
      final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> withdraw(target, currency, fixedAmount),
        true
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> balance(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return economyFacade.balance(player.getUniqueId(), currency, amount)
        .thenApply(state ->
            messageSource.balanceSet
                .with(USERNAME_VARIABLE_KEY, player.name())
                .with(CURRENCY_VARIABLE_KEY, currency.getSymbol())
                .with(AMOUNT_VARIABLE_KEY, getFormattedDecimal(amount))
        ).exceptionally(exception -> {
          throw new EconomyException(
              "Could not set balance of %s for %d to %.2f."
                  .formatted(
                      player.getUniqueId(),
                      currency.getId(),
                      amount
                  ),
              exception
          );
        });
  }

  private CompletableFuture<MutableMessage> deposit(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return economyFacade.deposit(player.getUniqueId(), currency, amount)
        .thenApply(state ->
            messageSource.balanceDeposited
                .with(USERNAME_VARIABLE_KEY, player.name())
                .with(CURRENCY_VARIABLE_KEY, currency.getSymbol())
                .with(AMOUNT_VARIABLE_KEY, getFormattedDecimal(amount))
        )
        .exceptionally(exception -> {
          throw new EconomyException(
              "Could not give %.2f to balance of %s for %d."
                  .formatted(
                      amount,
                      player.getUniqueId(),
                      currency.getId()
                  ),
              exception
          );
        });
  }

  private CompletableFuture<MutableMessage> withdraw(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return economyFacade.withdraw(player.getUniqueId(), currency, amount)
        .thenApply(state ->
            messageSource.balanceWithdrawn
                .with(USERNAME_VARIABLE_KEY, player.name())
                .with(CURRENCY_VARIABLE_KEY, currency.getSymbol())
                .with(AMOUNT_VARIABLE_KEY, getFormattedDecimal(amount))
        )
        .exceptionally(exception -> {
          throw new EconomyException(
              "Could not take %.2f from balance of %s for %d."
                  .formatted(
                      amount,
                      player.getUniqueId(),
                      currency.getId()
                  ),
              exception
          );
        });
  }

  private CompletableFuture<MutableMessage> processIncomingModification(
      final Long currencyId,
      final BigDecimal amount,
      final BiFunction<Currency, BigDecimal, CompletableFuture<MutableMessage>> modifier,
      final boolean requiresAmountValidation
  ) {
    final BigDecimal fixedAmount = amount.setScale(2, HALF_DOWN);
    if (requiresAmountValidation && fixedAmount.compareTo(ZERO) <= 0) {
      return messageSource.modificationAmountHasToBeGreaterThanZero
          .asCompletedFuture();
    }

    final Currency currency = currencyFacade.getCurrencyById(currencyId);
    if (currency == null) {
      return messageSource.modificationFailed
          .with(CURRENCY_ID_VARIABLE_KEY, currencyId)
          .asCompletedFuture();
    }

    return modifier.apply(currency, fixedAmount);
  }
}