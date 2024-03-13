package pl.auroramc.economy.transfer;

import static java.util.logging.Level.SEVERE;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.lazy.Lazy.lazy;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.lazy.Lazy;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;

@Permission("auroramc.economy.transfer")
@Route(name = "transfer", aliases = {"pay", "przelej", "zaplac"})
public class TransferCommand {

  private final Logger logger;
  private final EconomyFacade economyFacade;
  private final Lazy<Currency> transferCurrency;

  public TransferCommand(
      final Logger logger,
      final EconomyFacade economyFacade,
      final TransferConfig transferConfig,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.economyFacade = economyFacade;
    this.transferCurrency = lazy(
        () -> currencyFacade.getCurrencyById(transferConfig.transferableCurrencyId)
    );
  }

  @Execute
  public void transfer(
      final Player source, final @Arg Player target, final @Arg BigDecimal amount
  ) {
    final BigDecimal fixedAmount = amount.setScale(2, RoundingMode.HALF_DOWN);

    if (fixedAmount.compareTo(BigDecimal.ZERO) <= 0) {
      source.sendMessage(miniMessage().deserialize("<red>Kwota musi być większa od zera."));
      return;
    }

    if (source.getUniqueId().equals(target.getUniqueId())) {
      source.sendMessage(miniMessage().deserialize("<red>Nie możesz przelać pieniędzy samemu sobie."));
      return;
    }

    economyFacade.has(source.getUniqueId(), transferCurrency.get(), fixedAmount)
        .thenAccept(whetherTransferCouldBeFinalized -> processTransfer(
            source, target, fixedAmount, whetherTransferCouldBeFinalized))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void processTransfer(
      final Player source,
      final Player target,
      final BigDecimal amount,
      final boolean whetherTransferCouldBeFinalized
  ) {
    if (!whetherTransferCouldBeFinalized) {
      source.sendMessage(miniMessage().deserialize(
          "<red>Nie posiadasz wystarczającej ilości pieniędzy, aby wykonać ten przelew."));
      return;
    }

    final Currency resolvedCurrency = transferCurrency.get();
    final String preformattedAmount = getFormattedDecimal(amount);

    economyFacade.transfer(source.getUniqueId(), target.getUniqueId(), resolvedCurrency, amount)
        .thenAccept(state -> {
          source.sendMessage(
              miniMessage().deserialize(
                  "<gray>Wysłałeś przelew do <white><target><gray>, <gray>z twojego konta zostało odebrane <white><symbol><amount><gray>.",
                  unparsed("target", target.getName()),
                  unparsed("symbol", resolvedCurrency.getSymbol()),
                  unparsed("amount", preformattedAmount)
              )
          );
          target.sendMessage(
              miniMessage().deserialize(
                  "<gray>Otrzymałeś przelew od <white><source><gray>, do twojego konta zostało dodane <white><symbol><amount><gray>.",
                  unparsed("source", source.getName()),
                  unparsed("symbol", resolvedCurrency.getSymbol()),
                  unparsed("amount", preformattedAmount)
              )
          );
        })
        .exceptionally(exception -> {
          logger.log(SEVERE,
              "Could not transfer %s from %s to %s."
                  .formatted(
                      preformattedAmount,
                      source.getName(),
                      target.getName()
                  ),
              exception);
          source.sendMessage(
              miniMessage().deserialize(
                  "<red>Wystąpił błąd podczas wykonywania przelewu. Spróbuj ponownię wykonać przelew, jeśli błąd wystąpi ponownie zgłoś to administracji."
              )
          );
          return null;
        });
  }
}
