package pl.auroramc.economy.payment;

import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriod;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongFunction;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.message.MessageSource;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.economy.payments")
@Route(name = "payments")
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

  @Execute(route = "incoming", aliases = "in")
  public CompletableFuture<MutableMessage> incoming(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByReceiverId)
        .thenApply(payments -> {
          if (payments.isEmpty()) {
            return messageSource.noIncomingPayments
                .with("username", target.getName());
          }

          return messageSource.incomingPaymentsHeader
              .with("username", target.getName())
              .append(getParsedPayments(payments));
        });
  }

  @Execute(route = "outgoing", aliases = "out")
  public CompletableFuture<MutableMessage> outgoing(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByInitiatorId)
        .thenApply(payments -> {
          if (payments.isEmpty()) {
            return messageSource.noOutgoingPayments
                .with("username", target.getName());
          }

          return messageSource.outgoingPaymentsHeader
              .with("username", target.getName())
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
        .with("transaction_time", getFormattedPeriod(paymentSummary.transactionTime()))
        .with("id", paymentSummary.id().toString())
        .with("initiator", paymentSummary.initiatorUsername())
        .with("receiver", paymentSummary.receiverUsername())
        .with("symbol", paymentSummary.currencySymbol())
        .with("amount", getFormattedDecimal(paymentSummary.amount()));
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
