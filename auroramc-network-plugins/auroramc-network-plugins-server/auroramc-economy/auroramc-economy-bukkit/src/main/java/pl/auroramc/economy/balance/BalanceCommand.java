package pl.auroramc.economy.balance;

import static com.spotify.futures.CompletableFutures.joinList;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.economy.message.MutableMessageVariableKey.BALANCE_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.NAME_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.USERNAME_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.decimal.DecimalFormatter;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MutableMessageSource;

@Permission("auroramc.economy.balance")
@Command(name = "balance", aliases = {"bal", "saldo"})
public class BalanceCommand {

  private final Logger logger;
  private final EconomyFacade economyFacade;
  private final BalanceConfig balanceConfig;
  private final MutableMessageSource messageSource;
  private final CurrencyFacade currencyFacade;

  public BalanceCommand(
      final Logger logger,
      final EconomyFacade economyFacade,
      final BalanceConfig balanceConfig,
      final MutableMessageSource messageSource,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.economyFacade = economyFacade;
    this.balanceConfig = balanceConfig;
    this.messageSource = messageSource;
    this.currencyFacade = currencyFacade;
  }

  @Execute
  public CompletableFuture<MutableMessage> getBalance(
      final @Context Player player
  ) {
    return retrieveBalanceSummaries(player.getUniqueId())
        .thenApply(balanceSummaries ->
            messageSource.balanceSummaryHeader.append(balanceSummaries)
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Permission("auroramc.economy.balance.other")
  @Execute
  public CompletableFuture<MutableMessage> getBalance(
      final @Context Player player, final @Arg Player target
  ) {
    return retrieveBalanceSummaries(target.getUniqueId())
        .thenApply(balanceSummaries ->
            messageSource.balanceSummaryHeaderTargeted
                .with(USERNAME_VARIABLE_KEY, target.getName())
                .append(balanceSummaries)
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> retrieveBalanceSummaries(final UUID uniqueId) {
    return balanceConfig.summarizeCurrencyIds.stream()
        .map(currencyId -> retrieveBalanceSummary(uniqueId, currencyId))
        .collect(joinList())
        .thenApply(balanceSummaries -> balanceSummaries.stream().collect(MutableMessage.collector()))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> retrieveBalanceSummary(
      final UUID uniqueId, final Long currencyId
  ) {
    final Currency currency = currencyFacade.getCurrencyById(currencyId);
    if (currency == null) {
      throw new BalanceSummarizingException(
          "Could not summarize balance, because currency with id %s does not exist."
              .formatted(
                  currencyId
              )
      );
    }

    return economyFacade.balance(uniqueId, currency)
        .thenApply(DecimalFormatter::getFormattedDecimal)
        .thenApply(balance ->
            messageSource.balanceSummaryEntry
                .with(NAME_VARIABLE_KEY, currency.getName())
                .with(CURRENCY_VARIABLE_KEY, currency.getSymbol())
                .with(BALANCE_VARIABLE_KEY, balance)
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
