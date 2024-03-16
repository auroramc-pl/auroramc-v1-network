package pl.auroramc.economy.payment;

import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriod;
import static pl.auroramc.economy.message.MessageVariableKey.AMOUNT_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.INITIATOR_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.RECEIVER_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.TRANSACTION_ID_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.TRANSACTION_TIME_VARIABLE_KEY;
import static pl.auroramc.economy.message.MessageVariableKey.USERNAME_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongFunction;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.message.MessageSource;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.economy.payments")
@Command(name = "payments")
public class PaymentCommand {

  private final UserFacade userFacade;
  private final MessageSource messageSource;
  private final PaymentFacade paymentFacade;

  public PaymentCommand(
      final UserFacade userFacade,
      final MessageSource messageSource,
      final PaymentFacade paymentFacade
  ) {
    this.userFacade = userFacade;
    this.messageSource = messageSource;
    this.paymentFacade = paymentFacade;
  }

  @Execute(name = "incoming", aliases = "in")
  public CompletableFuture<MutableMessage> incoming(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByReceiverId)
        .thenApply(payments -> {
          if (payments.isEmpty()) {
            return messageSource.noIncomingPayments
                .with(USERNAME_VARIABLE_KEY, target.getName());
          }

          return messageSource.incomingPaymentsHeader
              .with(USERNAME_VARIABLE_KEY, target.getName())
              .append(getParsedPayments(payments));
        });
  }

  @Execute(name = "outgoing", aliases = "out")
  public CompletableFuture<MutableMessage> outgoing(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByInitiatorId)
        .thenApply(payments -> {
          if (payments.isEmpty()) {
            return messageSource.noOutgoingPayments
                .with(USERNAME_VARIABLE_KEY, target.getName());
          }

          return messageSource.outgoingPaymentsHeader
              .with(USERNAME_VARIABLE_KEY, target.getName())
              .append(getParsedPayments(payments));
        });
  }

  private MutableMessage getParsedPayments(final List<PaymentSummary> paymentSummaries) {
    return paymentSummaries.stream()
        .map(this::getParsedPayment)
        .collect(MutableMessage.collector());
  }

  private MutableMessage getParsedPayment(final PaymentSummary paymentSummary) {
    return messageSource.paymentEntry
        .with(TRANSACTION_ID_VARIABLE_KEY, paymentSummary.id().toString())
        .with(TRANSACTION_TIME_VARIABLE_KEY, getFormattedPeriod(paymentSummary.transactionTime()))
        .with(INITIATOR_VARIABLE_KEY, paymentSummary.initiatorUsername())
        .with(RECEIVER_VARIABLE_KEY, paymentSummary.receiverUsername())
        .with(CURRENCY_VARIABLE_KEY, paymentSummary.currencySymbol())
        .with(AMOUNT_VARIABLE_KEY, getFormattedDecimal(paymentSummary.amount()));
  }

  private CompletableFuture<List<PaymentSummary>> getPayments(
      final Player target,
      final LongFunction<CompletableFuture<List<PaymentSummary>>> paymentSummariesRetriever
  ) {
    return userFacade.getUserByUniqueId(target.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(paymentSummariesRetriever::apply);
  }
}
