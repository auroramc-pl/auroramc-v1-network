package pl.auroramc.cheque;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.decimal.DecimalUtils.getLengthOfFractionalPart;
import static pl.auroramc.commons.decimal.DecimalUtils.getLengthOfIntegralPart;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

@Permission("auroramc.cheques.cheque")
@Route(name = "cheque", aliases = {"czek", "banknot"})
class ChequeCommand {

  public static final int MAXIMUM_INTEGRAL_LENGTH = 9;
  public static final int MAXIMUM_FRACTION_LENGTH = 2;
  private static final BigDecimal MINIMUM_CHEQUE_WORTH = BigDecimal.valueOf(100);
  private static final BigDecimal MAXIMUM_CHEQUE_WORTH = BigDecimal.valueOf(1_000_000);
  private final Logger logger;
  private final MessageSource messageSource;
  private final ChequeFacade chequeFacade;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;

  public ChequeCommand(
      final Logger logger,
      final MessageSource messageSource,
      final ChequeFacade chequeFacade,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.chequeFacade = chequeFacade;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Execute
  public CompletableFuture<MutableMessage> cheque(
      final Player invoker, final @Arg BigDecimal amount
  ) {
    if (getLengthOfIntegralPart(amount) > 9 || getLengthOfFractionalPart(amount) > 2) {
      return messageSource.chequeCouldNotBeCreatedBecauseOfDigits
          .with("maximum-integral-length", MAXIMUM_INTEGRAL_LENGTH)
          .with("maximum-fraction-length", MAXIMUM_FRACTION_LENGTH)
          .asCompletedFuture();
    }

    if (amount.compareTo(MINIMUM_CHEQUE_WORTH) < 0 || amount.compareTo(MAXIMUM_CHEQUE_WORTH) > 0) {
      return messageSource.chequeCouldNotBeCreatedBecauseOfAmount
          .with("symbol", fundsCurrency.getSymbol())
          .with("minimum-cheque-worth", getFormattedDecimal(MINIMUM_CHEQUE_WORTH))
          .with("maximum-cheque-worth", getFormattedDecimal(MAXIMUM_CHEQUE_WORTH))
          .asCompletedFuture();
    }

    return economyFacade.has(invoker.getUniqueId(), fundsCurrency, amount)
        .thenCompose(whetherPlayerHasEnoughFunds ->
            completeChequeCreation(invoker, amount, whetherPlayerHasEnoughFunds))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> completeChequeCreation(
      final Player player, final BigDecimal amount, final boolean whetherPlayerHasEnoughFunds) {
    if (!whetherPlayerHasEnoughFunds) {
      return messageSource.chequeCouldNotBeCreatedBecauseOfMoney
          .asCompletedFuture();
    }

    return economyFacade.withdraw(player.getUniqueId(), fundsCurrency, amount)
        .thenApply(state -> new ChequeContext(new ChequeIssuer(player.getUniqueId(), player.getName()), amount))
        .thenApply(chequeFacade::createCheque)
        .thenAccept(chequeItem -> giveItemOrThrowIfFull(player, chequeItem))
        .thenApply(state -> messageSource.chequeIssued
            .with("symbol", fundsCurrency.getSymbol())
            .with("amount", getFormattedDecimal(amount)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void giveItemOrThrowIfFull(final Player target, final ItemStack itemStack) {
    target.getInventory()
        .addItem(itemStack)
        .forEach((index, remainingItem) ->
            target.getWorld().dropItemNaturally(target.getLocation(), remainingItem)
        );
  }
}
