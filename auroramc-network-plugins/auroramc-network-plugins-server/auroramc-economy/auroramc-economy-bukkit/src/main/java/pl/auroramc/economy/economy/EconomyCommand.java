package pl.auroramc.economy.economy;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.economy.economy.EconomyMessageSourcePaths.CONTEXT_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.bukkit.entity.Player;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.messages.message.MutableMessage;

@Permission("auroramc.economy.economy")
@Command(name = "economy", aliases = "eco")
public class EconomyCommand {

  private final EconomyFacade economyFacade;
  private final EconomyMessageSource messageSource;

  public EconomyCommand(
      final EconomyFacade economyFacade, final EconomyMessageSource messageSource) {
    this.economyFacade = economyFacade;
    this.messageSource = messageSource;
  }

  @Execute(name = "set")
  public CompletableFuture<MutableMessage> set(
      final @Arg Player target, final @Arg Currency currency, final @Arg BigDecimal amount) {
    return processIncomingModification(
        currency, amount, (ignored, fixedAmount) -> balance(target, currency, fixedAmount), false);
  }

  @Execute(name = "add")
  public CompletableFuture<MutableMessage> add(
      final @Arg Player target, final @Arg Currency currency, final @Arg BigDecimal amount) {
    return processIncomingModification(
        currency, amount, (ignored, fixedAmount) -> deposit(target, currency, fixedAmount), true);
  }

  @Execute(name = "take")
  public CompletableFuture<MutableMessage> take(
      final @Arg Player target, final @Arg Currency currency, final @Arg BigDecimal amount) {
    return processIncomingModification(
        currency, amount, (ignored, fixedAmount) -> withdraw(target, currency, fixedAmount), true);
  }

  private CompletableFuture<MutableMessage> balance(
      final Player player, final Currency currency, final BigDecimal amount) {
    return economyFacade
        .balance(player.getUniqueId(), currency, amount)
        .thenApply(
            ignored ->
                messageSource.balanceSet.placeholder(
                    CONTEXT_PATH, new EconomyContext(player, currency, amount)));
  }

  private CompletableFuture<MutableMessage> deposit(
      final Player player, final Currency currency, final BigDecimal amount) {
    return economyFacade
        .deposit(player.getUniqueId(), currency, amount)
        .thenApply(
            ignored ->
                messageSource.balanceDeposited.placeholder(
                    CONTEXT_PATH, new EconomyContext(player, currency, amount)));
  }

  private CompletableFuture<MutableMessage> withdraw(
      final Player player, final Currency currency, final BigDecimal amount) {
    return economyFacade
        .withdraw(player.getUniqueId(), currency, amount)
        .thenApply(
            ignored ->
                messageSource.balanceWithdrawn.placeholder(
                    CONTEXT_PATH, new EconomyContext(player, currency, amount)));
  }

  private CompletableFuture<MutableMessage> processIncomingModification(
      final Currency currency,
      final BigDecimal amount,
      final BiFunction<Currency, BigDecimal, CompletableFuture<MutableMessage>> modifier,
      final boolean requiresAmountValidation) {
    final BigDecimal fixedAmount = amount.setScale(2, HALF_DOWN);
    if (requiresAmountValidation && fixedAmount.compareTo(ZERO) <= 0) {
      return completedFuture(messageSource.validationRequiresAmountGreaterThanZero);
    }

    return modifier.apply(currency, fixedAmount);
  }
}
