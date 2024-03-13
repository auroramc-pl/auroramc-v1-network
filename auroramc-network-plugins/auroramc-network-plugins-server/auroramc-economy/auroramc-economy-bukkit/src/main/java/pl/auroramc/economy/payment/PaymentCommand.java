package pl.auroramc.economy.payment;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.period.PeriodFormatter.getFormattedPeriod;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.economy.payments")
@Route(name = "payments")
public class PaymentCommand {

  private final UserFacade userFacade;
  private final PaymentFacade paymentFacade;

  public PaymentCommand(
      final UserFacade userFacade,
      final PaymentFacade paymentFacade
  ) {
    this.userFacade = userFacade;
    this.paymentFacade = paymentFacade;
  }

  @Execute(route = "incoming", aliases = "in")
  public CompletableFuture<Component> incoming(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByReceiverId)
        .thenApply(payments ->
            miniMessage().deserialize(
                payments.isEmpty()
                    ? "<red>Gracz <yellow><target> <red>nie otrzymał jeszcze żadnych płatności przychodzących."
                    : "<gray>Płatności przychodzące dla <white><target><dark_gray>:<payments>",
                getPaymentTagResolvers(target, payments)
            )
        );
  }

  @Execute(route = "outgoing", aliases = "out")
  public CompletableFuture<Component> outgoing(final @Arg Player target) {
    return getPayments(target, paymentFacade::getPaymentSummariesByInitiatorId)
        .thenApply(payments ->
            miniMessage().deserialize(
                payments.isEmpty()
                    ? "<red>Gracz <yellow><target> <red>nie wykonał jeszcze żadnych płatności wychodzących."
                    : "<gray>Płatności wychodzące od <white><target><dark_gray>:<payments>",
                getPaymentTagResolvers(target, payments)
            )
        );
  }

  private TagResolver getPaymentTagResolvers(
      final Player target, final List<PaymentSummary> paymentSummaries) {
    return TagResolver.builder()
        .resolver(component("target", target.name()))
        .resolver(component("payments", getParsedPayments(paymentSummaries)))
        .build();
  }

  private Component getParsedPayments(final List<PaymentSummary> paymentSummaries) {
    Component reducer = empty();
    for (final PaymentSummary paymentSummary : paymentSummaries) {
      reducer = reducer
          .append(newline())
          .append(getParsedPayment(paymentSummary));
    }
    return reducer;
  }

  private Component getParsedPayment(final PaymentSummary paymentSummary) {
    return miniMessage().deserialize(
        "<gray>• <dark_gray><transaction_time> <dark_gray>(<white><id><dark_gray>) <dark_gray>(<white><initiator> <dark_gray>→ <white><receiver><dark_gray>) <dark_gray>- <white><amount>",
        unparsed("transaction_time", getFormattedPeriod(paymentSummary.transactionTime())),
        unparsed("id", paymentSummary.id().toString()),
        unparsed("initiator", paymentSummary.initiatorUsername()),
        unparsed("receiver", paymentSummary.receiverUsername()),
        unparsed("amount", getFormattedDecimal(paymentSummary.amount()))
    );
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
