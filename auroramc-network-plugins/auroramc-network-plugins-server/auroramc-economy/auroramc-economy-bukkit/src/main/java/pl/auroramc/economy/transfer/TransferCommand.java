package pl.auroramc.economy.transfer;

import static java.math.BigDecimal.ZERO;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.concurrent.CompletableFutureUtils.delegateCaughtException;
import static pl.auroramc.commons.lazy.Lazy.lazy;
import static pl.auroramc.economy.transfer.TransferMessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.economy.transfer.TransferMessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.messages.message.group.MutableMessageGroup.grouping;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.lazy.Lazy;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.group.MutableMessageGroup;

@Permission("auroramc.economy.transfer")
@Command(name = "transfer", aliases = "pay")
@Cooldown(key = "transfer-cooldown", count = 30, unit = SECONDS)
public class TransferCommand {

  private final EconomyFacade economyFacade;
  private final TransferMessageSource messageSource;
  private final Lazy<Currency> transferCurrency;

  public TransferCommand(
      final EconomyFacade economyFacade,
      final TransferMessageSource messageSource,
      final TransferConfig transferConfig,
      final CurrencyFacade currencyFacade) {
    this.economyFacade = economyFacade;
    this.messageSource = messageSource;
    this.transferCurrency =
        lazy(() -> currencyFacade.getCurrencyById(transferConfig.transferableCurrencyId));
  }

  @Execute
  public CompletableFuture<MutableMessageGroup> transfer(
      final @Context Player source, final @Arg Player target, final @Arg BigDecimal amount) {
    if (amount.compareTo(ZERO) <= 0) {
      return completedFuture(
          grouping().message(messageSource.validationRequiresAmountGreaterThanZero, source));
    }

    if (source.getUniqueId().equals(target.getUniqueId())) {
      return completedFuture(
          grouping().message(messageSource.validationRequiresSpecifyingTarget, source));
    }

    final Currency currency = transferCurrency.get();
    return economyFacade
        .has(source.getUniqueId(), currency, amount)
        .thenCompose(
            hasEnoughMoney -> processTransfer(currency, source, target, amount, hasEnoughMoney))
        .exceptionally(
            exception -> {
              delegateCaughtException(exception);
              return grouping()
                  .message(
                      messageSource.transferFailed.placeholder(CURRENCY_PATH, currency), source);
            });
  }

  private CompletableFuture<MutableMessageGroup> processTransfer(
      final Currency currency,
      final Player initiator,
      final Player receiver,
      final BigDecimal amount,
      final boolean hasEnoughMoney) {
    if (!hasEnoughMoney) {
      return completedFuture(
          grouping().message(messageSource.validationRequiresGreaterAmountOfBalance, initiator));
    }

    final TransferContext initiatorContext = new TransferContext(receiver, currency, amount);
    final TransferContext receiverContext = new TransferContext(initiator, currency, amount);

    return economyFacade
        .transfer(initiator.getUniqueId(), receiver.getUniqueId(), currency, amount)
        .thenApply(
            ignored ->
                grouping()
                    .message(
                        messageSource.transferSent.placeholder(CONTEXT_PATH, initiatorContext),
                        initiator)
                    .message(
                        messageSource.transferReceived.placeholder(CONTEXT_PATH, receiverContext),
                        receiver));
  }
}
