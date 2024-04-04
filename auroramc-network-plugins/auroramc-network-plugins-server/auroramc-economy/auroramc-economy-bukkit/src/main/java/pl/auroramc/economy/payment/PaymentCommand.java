package pl.auroramc.economy.payment;

import static pl.auroramc.economy.payment.PaymentDirection.INCOMING;
import static pl.auroramc.economy.payment.PaymentMessageSourcePaths.PAYMENT_PATH;
import static pl.auroramc.economy.payment.PaymentMessageSourcePaths.PLAYER_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.component.ComponentCollector;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.economy.payments")
@Command(name = "payments")
public class PaymentCommand {

  private final UserFacade userFacade;
  private final PaymentMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final PaymentFacade paymentFacade;

  public PaymentCommand(
      final UserFacade userFacade,
      final PaymentMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final PaymentFacade paymentFacade) {
    this.userFacade = userFacade;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.paymentFacade = paymentFacade;
  }

  @Execute
  public CompletableFuture<Component> payments(
      final @Arg Player target, final @Arg PaymentDirection direction) {
    return getPayments(target, direction)
        .thenApply(
            payments -> {
              if (payments.isEmpty()) {
                return messageCompiler
                    .compile(
                        (direction == INCOMING
                                ? messageSource.noIncomingPayments
                                : messageSource.noOutgoingPayments)
                            .placeholder(PLAYER_PATH, target))
                    .getComponent();
              }

              return getPaymentHeader(target, direction)
                  .appendNewline()
                  .append(getPaymentEntries(payments));
            });
  }

  private Component getPaymentHeader(final Player player, final PaymentDirection direction) {
    return messageCompiler
        .compile(
            (direction == INCOMING
                    ? messageSource.incomingPaymentsHeader
                    : messageSource.outgoingPaymentsHeader)
                .placeholder(PLAYER_PATH, player))
        .getComponent();
  }

  private Component getPaymentEntries(final List<PaymentSummary> paymentSummaries) {
    return paymentSummaries.stream()
        .map(this::getPaymentEntry)
        .collect(ComponentCollector.collector());
  }

  private Component getPaymentEntry(final PaymentSummary paymentSummary) {
    return messageCompiler
        .compile(messageSource.paymentEntry.placeholder(PAYMENT_PATH, paymentSummary))
        .getComponent();
  }

  private CompletableFuture<List<PaymentSummary>> getPayments(
      final Player player, final PaymentDirection direction) {
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(userId -> direction.getPaymentSummaries(paymentFacade, userId));
  }
}
