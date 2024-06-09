package pl.auroramc.cheque;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.cheque.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.cheque.message.MessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.cheque.message.MessageSourcePaths.MAXIMUM_CHEQUE_WORTH_PATH;
import static pl.auroramc.cheque.message.MessageSourcePaths.MAXIMUM_FRACTION_LENGTH_PATH;
import static pl.auroramc.cheque.message.MessageSourcePaths.MAXIMUM_INTEGRAL_LENGTH_PATH;
import static pl.auroramc.cheque.message.MessageSourcePaths.MINIMUM_CHEQUE_WORTH_PATH;
import static pl.auroramc.commons.format.decimal.DecimalUtils.getLengthOfFractionalPart;
import static pl.auroramc.commons.format.decimal.DecimalUtils.getLengthOfIntegralPart;
import static pl.auroramc.integrations.item.ItemStackUtils.giveOrDropItemStack;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.MutableMessage;

@Permission("auroramc.cheques.cheque")
@Command(name = "cheque", aliases = "czek")
@Cooldown(key = "cheque-cooldown", count = 30, unit = SECONDS)
class ChequeCommand {

  private static final int MAXIMUM_INTEGRAL_LENGTH = 9;
  private static final int MAXIMUM_FRACTION_LENGTH = 2;
  private static final BigDecimal MINIMUM_CHEQUE_WORTH = BigDecimal.valueOf(100);
  private static final BigDecimal MAXIMUM_CHEQUE_WORTH = BigDecimal.valueOf(1_000_000);
  private final MessageSource messageSource;
  private final ChequeFacade chequeFacade;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;

  public ChequeCommand(
      final MessageSource messageSource,
      final ChequeFacade chequeFacade,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade) {
    this.messageSource = messageSource;
    this.chequeFacade = chequeFacade;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Execute
  public CompletableFuture<MutableMessage> cheque(
      final @Context Player player, final @Arg BigDecimal amount) {
    if (getLengthOfIntegralPart(amount) > 9 || getLengthOfFractionalPart(amount) > 2) {
      return completedFuture(
          messageSource
              .validationRequiresIntegralAndFractionalInBounds
              .placeholder(MAXIMUM_INTEGRAL_LENGTH_PATH, MAXIMUM_INTEGRAL_LENGTH)
              .placeholder(MAXIMUM_FRACTION_LENGTH_PATH, MAXIMUM_FRACTION_LENGTH));
    }

    if (amount.compareTo(MINIMUM_CHEQUE_WORTH) < 0 || amount.compareTo(MAXIMUM_CHEQUE_WORTH) > 0) {
      return completedFuture(
          messageSource
              .validationRequiresAmountInBounds
              .placeholder(CURRENCY_PATH, fundsCurrency)
              .placeholder(MINIMUM_CHEQUE_WORTH_PATH, MINIMUM_CHEQUE_WORTH)
              .placeholder(MAXIMUM_CHEQUE_WORTH_PATH, MAXIMUM_CHEQUE_WORTH));
    }

    return economyFacade
        .has(player.getUniqueId(), fundsCurrency, amount)
        .thenCompose(
            whetherPlayerHasEnoughFunds ->
                completeChequeCreation(player, amount, whetherPlayerHasEnoughFunds));
  }

  private CompletableFuture<MutableMessage> completeChequeCreation(
      final Player player, final BigDecimal amount, final boolean whetherPlayerHasEnoughFunds) {
    if (!whetherPlayerHasEnoughFunds) {
      return completedFuture(messageSource.validationRequiresGreaterAmountOfBalance);
    }

    final ChequeIssuer chequeIssuer = new ChequeIssuer(player.getUniqueId(), player.getName());
    final ChequeContext chequeContext = new ChequeContext(chequeIssuer, fundsCurrency, amount);
    return economyFacade
        .withdraw(player.getUniqueId(), fundsCurrency, amount)
        .thenApply(state -> chequeFacade.createCheque(chequeContext))
        .thenAccept(chequeItem -> giveOrDropItemStack(player, chequeItem))
        .thenApply(state -> messageSource.chequeIssued.placeholder(CONTEXT_PATH, chequeContext))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
