package pl.auroramc.bazaars.bazaar;

import java.util.concurrent.CompletableFuture;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.MutableMessage;

public interface BazaarFacade {

  static BazaarFacade getBazaarFacade(
      final Scheduler scheduler,
      final MessageSource messageSource,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency) {
    return new BazaarService(scheduler, messageSource, economyFacade, fundsCurrency);
  }

  CompletableFuture<MutableMessage> handleItemTransaction(
      final BazaarTransactionContext transactionContext);

  CompletableFuture<MutableMessage> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds);

  CompletableFuture<MutableMessage> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds);
}
