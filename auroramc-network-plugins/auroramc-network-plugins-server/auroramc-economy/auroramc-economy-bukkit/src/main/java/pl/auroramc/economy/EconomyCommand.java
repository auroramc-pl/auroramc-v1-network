package pl.auroramc.economy;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MessageSource;

@Permission("auroramc.economy.economy")
@Route(name = "economy", aliases = "eco")
public class EconomyCommand {

  private final Logger logger;
  private final MessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final CurrencyFacade currencyFacade;

  public EconomyCommand(
      final Logger logger,
      final MessageSource messageSource,
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  @Execute(route = "set")
  public CompletableFuture<MutableMessage> set(
      final @Arg Player target, final @Arg Long currencyId, final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> balance(target, currency, fixedAmount),
        false
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Execute(route = "add")
  public CompletableFuture<MutableMessage> add(
      final @Arg Player target, final @Arg Long currencyId, final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> deposit(target, currency, fixedAmount),
        true
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Execute(route = "take")
  public CompletableFuture<MutableMessage> take(
      final @Arg Player target, final @Arg Long currencyId, final @Arg BigDecimal amount
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
                .with("username", player.name())
                .with("symbol", currency.getSymbol())
                .with("amount", getFormattedDecimal(amount))
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
                .with("username", player.name())
                .with("symbol", currency.getSymbol())
                .with("amount", getFormattedDecimal(amount))
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
                .with("username", player.name())
                .with("symbol", currency.getSymbol())
                .with("amount", getFormattedDecimal(amount))
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
    final BigDecimal fixedAmount = amount.setScale(2, RoundingMode.HALF_DOWN);
    if (requiresAmountValidation && fixedAmount.compareTo(BigDecimal.ZERO) <= 0) {
      return messageSource.modificationAmountHasToBeGreaterThanZero
          .asCompletedFuture();
    }

    final Currency currency = currencyFacade.getCurrencyById(currencyId);
    if (currency == null) {
      return messageSource.modificationFailed
          .with("currency_id", currencyId)
          .asCompletedFuture();
    }

    return modifier.apply(currency, fixedAmount);
  }
}