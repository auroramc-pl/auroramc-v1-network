package pl.auroramc.bazaars.bazaar;

import java.util.concurrent.CompletableFuture;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;

public interface BazaarFacade {

  static BazaarFacade getBazaarFacade(
      final Scheduler scheduler,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency) {
    return new BazaarService(
        scheduler, messageSource, messageCompiler, economyFacade, fundsCurrency);
  }

  CompletableFuture<CompiledMessage> handleItemTransaction(
      final BazaarTransactionContext transactionContext);

  CompletableFuture<CompiledMessage> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds);

  CompletableFuture<CompiledMessage> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds);
}
