package pl.auroramc.economy;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;

@Permission("auroramc.economy.economy")
@Route(name = "economy", aliases = "eco")
public class EconomyCommand {

  private final Logger logger;
  private final EconomyFacade economyFacade;
  private final CurrencyFacade currencyFacade;

  public EconomyCommand(
      final Logger logger,
      final EconomyFacade economyFacade,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.economyFacade = economyFacade;
    this.currencyFacade = currencyFacade;
  }

  @Execute(route = "set")
  public CompletableFuture<Component> balance(
      final @Arg Player target, final @Arg Long currencyId, final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> balance(target, currency, fixedAmount),
        false
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Execute(route = "deposit", aliases = {"give", "add"})
  public CompletableFuture<Component> deposit(
      final @Arg Player target, final @Arg Long currencyId, final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> deposit(target, currency, fixedAmount),
        true
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Execute(route = "withdraw", aliases = {"take", "remove"})
  public CompletableFuture<Component> withdraw(
      final @Arg Player target, final @Arg Long currencyId, final @Arg BigDecimal amount
  ) {
    return processIncomingModification(currencyId, amount,
        (currency, fixedAmount) -> withdraw(target, currency, fixedAmount),
        true
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<Component> balance(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return economyFacade.balance(player.getUniqueId(), currency, amount)
        .thenApply(state ->
            miniMessage().deserialize(
                "<gray>Ustawiono saldo gracza <white><player><gray> dla waluty <white><currency_name> <dark_gray>(<white><currency_id><dark_gray>) <gray>na <white><symbol><amount><gray>.",
                getModificationTagResolvers(player, currency, amount)
            )
        ).exceptionally(exception -> {
          throw new EconomyException(
              "Could not set balance of %s for %d to %.2f."
                  .formatted(
                      player.getUniqueId(),
                      currency.getId(),
                      amount
                  ),
              exception
          );
        });
  }

  private CompletableFuture<Component> deposit(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return economyFacade.deposit(player.getUniqueId(), currency, amount)
        .thenApply(state ->
            miniMessage().deserialize(
                "<gray>Dodano <white><symbol><amount> <gray>do salda gracza <white><player><gray> dla waluty <white><currency_name> <dark_gray>(<white><currency_id><dark_gray>)<gray>.",
                getModificationTagResolvers(player, currency, amount)
            )
        )
        .exceptionally(exception -> {
          throw new EconomyException(
              "Could not give %.2f to balance of %s for %d."
                  .formatted(
                      amount,
                      player.getUniqueId(),
                      currency.getId()
                  ),
              exception
          );
        });
  }

  private CompletableFuture<Component> withdraw(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return economyFacade.withdraw(player.getUniqueId(), currency, amount)
        .thenApply(state ->
            miniMessage().deserialize(
                "<gray>Odebrano <white><symbol><amount> <gray>z salda gracza <white><player><gray> dla waluty <white><currency_name> <dark_gray>(<white><currency_id><dark_gray>)<gray>.",
                getModificationTagResolvers(player, currency, amount)
            )
        )
        .exceptionally(exception -> {
          throw new EconomyException(
              "Could not take %.2f from balance of %s for %d."
                  .formatted(
                      amount,
                      player.getUniqueId(),
                      currency.getId()
                  ),
              exception
          );
        });
  }

  private TagResolver[] getModificationTagResolvers(
      final Player player, final Currency currency, final BigDecimal amount
  ) {
    return
        List.of(
            component("player", player.name()),
            unparsed("currency_name", currency.getName()),
            unparsed("currency_id", String.valueOf(currency.getId())),
            unparsed("symbol", currency.getSymbol()),
            unparsed("amount", getFormattedDecimal(amount))
        ).toArray(TagResolver[]::new);
  }

  private CompletableFuture<Component> processIncomingModification(
      final Long currencyId,
      final BigDecimal amount,
      final BiFunction<Currency, BigDecimal, CompletableFuture<Component>> modificationFunction,
      final boolean requiresAmountValidation
  ) {
    final BigDecimal fixedAmount = amount.setScale(2, RoundingMode.HALF_DOWN);
    if (requiresAmountValidation && fixedAmount.compareTo(BigDecimal.ZERO) <= 0) {
      return completedFuture(miniMessage().deserialize("<red>Kwota musi być większa od zera."));
    }

    final Currency currency = currencyFacade.getCurrencyById(currencyId);
    if (currency == null) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Operacja nie została wykonana, gdyż nie udało się odnaleźć waluty z id pasującym do <yellow><currency_id><red>.",
              unparsed("currency_id", String.valueOf(currencyId))
          )
      );
    }

    return modificationFunction.apply(currency, fixedAmount);
  }
}