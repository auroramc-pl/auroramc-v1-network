package pl.auroramc.bazaars.bazaar;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.getEmptySlotsCount;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.getQuantityInSlots;
import static pl.auroramc.bazaars.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.bazaars.message.MessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.bazaars.message.MessageSourcePaths.PRODUCT_PATH;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;

import java.util.concurrent.CompletableFuture;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.MutableMessage;

class BazaarService implements BazaarFacade {

  private final Scheduler scheduler;
  private final MessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final Currency fundsCurrency;

  BazaarService(
      final Scheduler scheduler,
      final MessageSource messageSource,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency) {
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
  }

  @Override
  public CompletableFuture<MutableMessage> handleItemTransaction(
      final BazaarTransactionContext transactionContext) {
    switch (transactionContext.parsingContext().type()) {
      case BUY -> {
        return economyFacade
            .has(
                transactionContext.customerUniqueId(),
                fundsCurrency,
                transactionContext.parsingContext().price())
            .thenCompose(
                whetherCustomerHasEnoughFunds ->
                    handleItemPurchase(transactionContext, whetherCustomerHasEnoughFunds));
      }
      case SELL -> {
        return economyFacade
            .has(
                transactionContext.merchantUniqueId(),
                fundsCurrency,
                transactionContext.parsingContext().price())
            .thenCompose(
                whetherMerchantHasEnoughFunds ->
                    handleItemSale(transactionContext, whetherMerchantHasEnoughFunds));
      }
      default ->
          throw new IllegalStateException("Could not parse bazaar, because of malformed type.");
    }
  }

  @Override
  public CompletableFuture<MutableMessage> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds) {
    if (!whetherCustomerHasEnoughFunds) {
      return completedFuture(messageSource.customerOutOfBalance);
    }

    final BazaarParsingContext parsingContext = transactionContext.parsingContext();

    final int requiredSlots =
        getQuantityInSlots(parsingContext.quantity(), parsingContext.material().getMaxStackSize());
    final int obtainedSlots =
        getEmptySlotsCount(transactionContext.customer().getInventory(), parsingContext.material());
    if (requiredSlots > obtainedSlots) {
      return completedFuture(messageSource.customerOutOfSpace);
    }

    return economyFacade
        .transfer(
            transactionContext.customerUniqueId(),
            transactionContext.merchantUniqueId(),
            fundsCurrency,
            parsingContext.price())
        .thenAccept(
            state -> scheduler.run(SYNC, () -> handleItemTransferForPurchase(transactionContext)))
        .thenApply(
            state ->
                messageSource
                    .productBought
                    .placeholder(CURRENCY_PATH, fundsCurrency)
                    .placeholder(CONTEXT_PATH, parsingContext)
                    .placeholder(
                        PRODUCT_PATH,
                        new ItemStack(parsingContext.material(), parsingContext.quantity())));
  }

  @Override
  public CompletableFuture<MutableMessage> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();

    final boolean whetherCustomerHasEnoughStock =
        transactionContext
            .customer()
            .getInventory()
            .containsAtLeast(new ItemStack(parsingContext.material()), parsingContext.quantity());
    if (!whetherCustomerHasEnoughStock) {
      return completedFuture(messageSource.customerOutOfProduct);
    }

    if (!whetherMerchantHasEnoughFunds) {
      return completedFuture(messageSource.merchantOutOfBalance);
    }

    final int requiredSlots =
        getQuantityInSlots(parsingContext.quantity(), parsingContext.material().getMaxStackSize());
    final int obtainedSlots =
        getEmptySlotsCount(transactionContext.magazine().getInventory(), parsingContext.material());
    if (requiredSlots > obtainedSlots) {
      return completedFuture(messageSource.bazaarOutOfSpace);
    }

    return economyFacade
        .transfer(
            transactionContext.merchantUniqueId(),
            transactionContext.customerUniqueId(),
            fundsCurrency,
            parsingContext.price())
        .thenAccept(
            state -> scheduler.run(SYNC, () -> handleItemTransferForSale(transactionContext)))
        .thenApply(
            state ->
                messageSource
                    .productSold
                    .placeholder(CURRENCY_PATH, fundsCurrency)
                    .placeholder(CONTEXT_PATH, parsingContext)
                    .placeholder(
                        PRODUCT_PATH,
                        new ItemStack(parsingContext.material(), parsingContext.quantity())))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void handleItemTransferForPurchase(final @NotNull BazaarTransactionContext transactionContext) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();
    final ItemStack productItemStack =
        new ItemStack(parsingContext.material(), parsingContext.quantity());
    transactionContext.magazine().getInventory().removeItemAnySlot(productItemStack);
    transactionContext
        .customer()
        .getInventory()
        .addItem(productItemStack)
        .forEach(
            (index, remainingItem) ->
                transactionContext
                    .customer()
                    .getWorld()
                    .dropItemNaturally(transactionContext.customer().getLocation(), remainingItem));
  }

  private void handleItemTransferForSale(final BazaarTransactionContext transactionContext) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();
    final ItemStack productItemStack =
        new ItemStack(parsingContext.material(), parsingContext.quantity());
    transactionContext.customer().getInventory().removeItemAnySlot(productItemStack);
    transactionContext.magazine().getInventory().addItem(productItemStack);
  }
}
