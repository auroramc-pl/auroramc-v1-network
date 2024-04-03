package pl.auroramc.economy.payment;

import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriod;
import static pl.auroramc.economy.message.MutableMessageVariableKey.AMOUNT_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.INITIATOR_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.RECEIVER_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.TRANSACTION_ID_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.TRANSACTION_TIME_PATH;
import static pl.auroramc.economy.message.MutableMessageVariableKey.USERNAME_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongFunction;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.message.MutableMessageSource;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.economy.payments")
@Command(name = "payments")
public class PaymentCommand {

  private final UserFacade userFacade;
  private final MutableMessageSource messageSource;
  private final PaymentFacade paymentFacade;

  public PaymentCommand(
      final UserFacade userFacade,
      final MutableMessageSource messageSource,
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
                .with(USERNAME_PATH, target.getName());
          }

          return messageSource.incomingPaymentsHeader
              .with(USERNAME_PATH, target.getName())
              .append(getParsedPayments(payments));
        });
  }

  @Execute(name = "outgoing", aliases = "out")
  public CompletableFuture<MutableMessage> outgoing(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByInitiatorId)
        .thenApply(payments -> {
          if (payments.isEmpty()) {
            return messageSource.noOutgoingPayments
                .with(USERNAME_PATH, target.getName());
          }

          return messageSource.outgoingPaymentsHeader
              .with(USERNAME_PATH, target.getName())
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
        .with(TRANSACTION_ID_PATH, paymentSummary.id().toString())
        .with(TRANSACTION_TIME_PATH, getFormattedPeriod(paymentSummary.transactionTime()))
        .with(INITIATOR_PATH, paymentSummary.initiatorUsername())
        .with(RECEIVER_PATH, paymentSummary.receiverUsername())
        .with(CURRENCY_PATH, paymentSummary.currencySymbol())
        .with(AMOUNT_PATH, getFormattedDecimal(paymentSummary.amount()));
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
