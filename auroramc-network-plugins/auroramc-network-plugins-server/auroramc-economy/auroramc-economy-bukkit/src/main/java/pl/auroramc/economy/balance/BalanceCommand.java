package pl.auroramc.economy.balance;

import static com.spotify.futures.CompletableFutures.joinList;
import static java.time.temporal.ChronoUnit.SECONDS;
import static pl.auroramc.economy.balance.BalanceMessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.economy.balance.BalanceMessageSourcePaths.PLAYER_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.messages.message.component.ComponentCollector;

@Permission("auroramc.economy.balance")
@Command(name = "balance", aliases = "bal")
@Cooldown(key = "balance-cooldown", count = 5, unit = SECONDS)
public class BalanceCommand {

  private final EconomyFacade economyFacade;
  private final BalanceConfig balanceConfig;
  private final BalanceMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final CurrencyFacade currencyFacade;

  public BalanceCommand(
      final EconomyFacade economyFacade,
      final BalanceConfig balanceConfig,
      final BalanceMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final CurrencyFacade currencyFacade) {
    this.economyFacade = economyFacade;
    this.balanceConfig = balanceConfig;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.currencyFacade = currencyFacade;
  }

  @Execute
  public CompletableFuture<Component> balance(final @Context Player player) {
    return retrieveBalanceSummaries(player.getUniqueId())
        .thenApply(
            balanceSummaries ->
                messageCompiler
                    .compile(messageSource.balanceSummaryHeader)
                    .getComponent()
                    .appendNewline()
                    .append(balanceSummaries));
  }

  @Permission("auroramc.economy.balance.other")
  @Execute
  public CompletableFuture<Component> balanceOther(
      final @Context Player player, final @Arg Player target) {
    return retrieveBalanceSummaries(target.getUniqueId())
        .thenApply(
            balanceSummaries ->
                messageCompiler
                    .compile(
                        messageSource.balanceSummaryHeaderTargeted.placeholder(
                            PLAYER_PATH, target.getName()))
                    .getComponent()
                    .appendNewline()
                    .append(balanceSummaries));
  }

  private CompletableFuture<Component> retrieveBalanceSummaries(final UUID uniqueId) {
    return balanceConfig.summarizeCurrencyIds.stream()
        .map(currencyId -> retrieveBalanceSummary(uniqueId, currencyId))
        .collect(joinList())
        .thenApply(
            balanceSummaries -> balanceSummaries.stream().collect(ComponentCollector.collector()));
  }

  private CompletableFuture<Component> retrieveBalanceSummary(
      final UUID uniqueId, final Long currencyId) {
    final Currency currency = currencyFacade.getCurrencyById(currencyId);
    if (currency == null) {
      throw new BalanceSummarizingException(
          "Could not summarize balance, because currency with id %s does not exist."
              .formatted(currencyId));
    }

    return economyFacade
        .balance(uniqueId, currency)
        .thenApply(
            balance ->
                messageSource.balanceSummaryEntry.placeholder(
                    CONTEXT_PATH, new BalanceContext(currency, balance)))
        .thenApply(messageCompiler::compile)
        .thenApply(CompiledMessage::getComponent);
  }
}
