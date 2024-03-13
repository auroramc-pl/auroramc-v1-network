package pl.auroramc.economy.balance;

import static com.spotify.futures.CompletableFutures.joinList;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.commons.decimal.DecimalFormatter;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;

@Permission("auroramc.economy.balance")
@Route(name = "balance", aliases = {"bal", "saldo"})
public class BalanceCommand {

  private final Logger logger;
  private final EconomyFacade economyFacade;
  private final BalanceConfig balanceConfig;
  private final CurrencyFacade currencyFacade;

  public BalanceCommand(
      final Logger logger,
      final EconomyFacade economyFacade,
      final BalanceConfig balanceConfig,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.economyFacade = economyFacade;
    this.balanceConfig = balanceConfig;
    this.currencyFacade = currencyFacade;
  }

  @Execute(required = 0)
  public CompletableFuture<Component> getBalance(final Player executor) {
    return retrieveBalanceSummaries(executor.getUniqueId())
        .thenApply(balanceSummaries -> miniMessage().deserialize(
            "<gray>Podsumowanie kont: <newline><white><summaries>",
                component("summaries", balanceSummaries)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Permission("auroramc.economy.balance.other")
  @Execute
  public CompletableFuture<Component> getBalance(final Player executor, final @Arg Player target) {
    return retrieveBalanceSummaries(target.getUniqueId())
        .thenApply(balanceSummaries -> miniMessage().deserialize(
            "<gray>Podsumowanie kont gracza <white><target><gray>: <newline><white><summaries>",
                component("target", target.name()),
                component("summaries", balanceSummaries)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<Component> retrieveBalanceSummaries(final UUID uniqueId) {
    return balanceConfig.summarizeCurrencyIds.stream()
        .map(currencyId -> retrieveBalanceSummary(uniqueId, currencyId))
        .collect(joinList())
        .thenApply(balanceSummaries -> balanceSummaries
            .stream()
            .reduce(empty(), Component::append))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<Component> retrieveBalanceSummary(
      final UUID uniqueId, final Long currencyId
  ) {
    final Currency currency = currencyFacade.getCurrencyById(currencyId);
    if (currency == null) {
      throw new BalanceSummarizingException(
          "Could not summarize balance, because currency with id %s does not exist.".formatted(
              currencyId));
    }

    return economyFacade.balance(uniqueId, currency)
        .thenApply(DecimalFormatter::getFormattedDecimal)
        .thenApply(balance -> miniMessage().deserialize("<dark_gray>► <gray><currency_name> <dark_gray>- <white><currency_symbol><balance>",
            unparsed("currency_name", currency.getName()),
            unparsed("currency_symbol", currency.getSymbol()),
            unparsed("balance", balance)))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
