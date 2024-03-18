package pl.auroramc.economy.transfer;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.logging.Level.SEVERE;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.lazy.Lazy.lazy;
import static pl.auroramc.economy.message.MutableMessageVariableKey.AMOUNT_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.SOURCE_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.economy.message.MutableMessageVariableKey.TARGET_VARIABLE_KEY;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import pl.auroramc.commons.lazy.Lazy;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.currency.CurrencyFacade;
import pl.auroramc.economy.message.MutableMessageSource;

@Permission("auroramc.economy.transfer")
@Command(name = "transfer", aliases = {"pay", "przelej", "zaplac"})
public class TransferCommand {

  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final Lazy<Currency> transferCurrency;

  public TransferCommand(
      final Logger logger,
      final MutableMessageSource messageSource,
      final EconomyFacade economyFacade,
      final TransferConfig transferConfig,
      final CurrencyFacade currencyFacade
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
    this.transferCurrency = lazy(
        () -> currencyFacade.getCurrencyById(transferConfig.transferableCurrencyId)
    );
  }

  @Execute
  public void transfer(
      final @Context Player source,
      final @Arg Player target,
      final @Arg BigDecimal amount
  ) {
    final BigDecimal fixedAmount = amount.setScale(2, HALF_DOWN);
    if (fixedAmount.compareTo(ZERO) <= 0) {
      source.sendMessage(
          messageSource.transferAmountHasToBeGreaterThanZero
              .compile()
      );
      return;
    }

    if (source.getUniqueId().equals(target.getUniqueId())) {
      source.sendMessage(
          messageSource.transferRequiresTarget
              .compile()
      );
      return;
    }

    economyFacade.has(source.getUniqueId(), transferCurrency.get(), fixedAmount)
        .thenAccept(whetherTransferCouldBeFinalized ->
            processTransfer(
                source, target, fixedAmount, whetherTransferCouldBeFinalized
            )
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void processTransfer(
      final Player source,
      final Player target,
      final BigDecimal amount,
      final boolean whetherTransferCouldBeFinalized
  ) {
    if (!whetherTransferCouldBeFinalized) {
      source.sendMessage(
          messageSource.transferMissingBalance
              .compile()
      );
      return;
    }

    final Currency resolvedCurrency = transferCurrency.get();
    final String preformattedAmount = getFormattedDecimal(amount);

    economyFacade.transfer(source.getUniqueId(), target.getUniqueId(), resolvedCurrency, amount)
        .thenAccept(state -> {
          source.sendMessage(
              messageSource.transferSent
                  .with(TARGET_VARIABLE_KEY, target.getName())
                  .with(CURRENCY_VARIABLE_KEY, resolvedCurrency.getSymbol())
                  .with(AMOUNT_VARIABLE_KEY, preformattedAmount)
                  .compile()
          );
          target.sendMessage(
              messageSource.transferReceived
                  .with(SOURCE_VARIABLE_KEY, source.getName())
                  .with(CURRENCY_VARIABLE_KEY, resolvedCurrency.getSymbol())
                  .with(AMOUNT_VARIABLE_KEY, preformattedAmount)
                  .compile()
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
              exception
          );
          source.sendMessage(
              messageSource.transferFailed
                  .compile()
          );
          return null;
        });
  }
}
