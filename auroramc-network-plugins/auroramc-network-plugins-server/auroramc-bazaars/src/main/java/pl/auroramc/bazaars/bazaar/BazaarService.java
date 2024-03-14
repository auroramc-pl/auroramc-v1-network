package pl.auroramc.bazaars.bazaar;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.getEmptySlotsCount;
import static pl.auroramc.bazaars.bazaar.BazaarUtils.getQuantityInSlots;
import static pl.auroramc.commons.BukkitUtils.postToMainThread;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bazaars.bazaar.parser.BazaarParsingContext;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.bazaars.message.MessageSource;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

class BazaarService implements BazaarFacade {

  private final Plugin plugin;
  private final DecimalFormat priceFormat;
  private final MessageSource messageSource;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;

  BazaarService(
      final Plugin plugin,
      final DecimalFormat priceFormat,
      final MessageSource messageSource,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade
  ) {
    this.plugin = plugin;
    this.priceFormat = priceFormat;
    this.messageSource = messageSource;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Override
  public CompletableFuture<MutableMessage> handleItemTransaction(
      final BazaarTransactionContext transactionContext
  ) {
    switch (transactionContext.parsingContext().type()) {
      case BUY -> {
        return economyFacade
            .has(
                transactionContext.customerUniqueId(),
                fundsCurrency,
                transactionContext.parsingContext().price()
            )
            .thenCompose(whetherCustomerHasEnoughFunds ->
                handleItemPurchase(transactionContext, whetherCustomerHasEnoughFunds)
            );
      }
      case SELL -> {
        return economyFacade
            .has(
                transactionContext.merchantUniqueId(),
                fundsCurrency,
                transactionContext.parsingContext().price()
            )
            .thenCompose(
                whetherMerchantHasEnoughFunds -> handleItemSale(transactionContext, whetherMerchantHasEnoughFunds)
            );
      }
      default ->
          throw new IllegalStateException("Could not parse bazaar, because of malformed type.");
    }
  }

  @Override
  public CompletableFuture<MutableMessage> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds
  ) {
    if (!whetherCustomerHasEnoughFunds) {
      return completedFuture(messageSource.customerOutOfBalance);
    }

    final BazaarParsingContext parsingContext = transactionContext.parsingContext();

    final int requiredSlots = getQuantityInSlots(
        parsingContext.quantity(),
        parsingContext.material().getMaxStackSize()
    );
    final int obtainedSlots = getEmptySlotsCount(
        transactionContext.customer().getInventory(), parsingContext.material()
    );
    if (requiredSlots > obtainedSlots) {
      return completedFuture(messageSource.customerOutOfSpace);
    }

    return economyFacade
        .transfer(
            transactionContext.customerUniqueId(),
            transactionContext.merchantUniqueId(),
            fundsCurrency,
            parsingContext.price()
        )
        .thenAccept(state ->
            postToMainThread(plugin, () ->
                handleItemTransferForPurchase(transactionContext)
            )
        )
        .thenApply(state ->
            messageSource.productBought
                .with("quantity", parsingContext.quantity())
                .with("material", capitalize(parsingContext.material().name().toLowerCase(Locale.ROOT)))
                .with("merchant", parsingContext.merchant())
                .with("symbol", fundsCurrency.getSymbol())
                .with("price", priceFormat.format(parsingContext.price()))
        );
  }

  @Override
  public CompletableFuture<MutableMessage> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds
  ) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();

    final boolean whetherCustomerHasEnoughStock = transactionContext.customer()
        .getInventory()
        .containsAtLeast(new ItemStack(parsingContext.material()), parsingContext.quantity());
    if (!whetherCustomerHasEnoughStock) {
      return completedFuture(messageSource.customerOutOfProduct);
    }

    if (!whetherMerchantHasEnoughFunds) {
      return completedFuture(messageSource.merchantOutOfBalance);
    }

    final int requiredSlots = getQuantityInSlots(
        parsingContext.quantity(),
        parsingContext.material().getMaxStackSize()
    );
    final int obtainedSlots = getEmptySlotsCount(
        transactionContext.magazine().getInventory(), parsingContext.material()
    );
    if (requiredSlots > obtainedSlots) {
      return completedFuture(messageSource.bazaarOutOfSpace);
    }

    return economyFacade
        .transfer(
            transactionContext.merchantUniqueId(),
            transactionContext.customerUniqueId(),
            fundsCurrency,
            parsingContext.price()
        )
        .thenAccept(state ->
            postToMainThread(plugin,
                () -> handleItemTransferForSale(transactionContext)
            )
        )
        .thenApply(state ->
            messageSource.productSold
                .with("quantity", parsingContext.quantity())
                .with("material", capitalize(parsingContext.material().name().toLowerCase(Locale.ROOT)))
                .with("merchant", parsingContext.merchant())
                .with("symbol", fundsCurrency.getSymbol())
                .with("price", priceFormat.format(parsingContext.price()))
        );
  }

  private void handleItemTransferForPurchase(final BazaarTransactionContext transactionContext) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();
    final ItemStack productItemStack = new ItemStack(parsingContext.material(), parsingContext.quantity());
    transactionContext.magazine().getInventory().removeItemAnySlot(productItemStack);
    transactionContext.customer().getInventory().addItem(productItemStack)
        .forEach((index, remainingItem) ->
            transactionContext.customer()
                .getWorld()
                .dropItemNaturally(transactionContext.customer().getLocation(), remainingItem)
        );
  }

  private void handleItemTransferForSale(final BazaarTransactionContext transactionContext) {
    final BazaarParsingContext parsingContext = transactionContext.parsingContext();
    final ItemStack productItemStack = new ItemStack(parsingContext.material(), parsingContext.quantity());
    transactionContext.customer().getInventory().removeItemAnySlot(productItemStack);
    transactionContext.magazine().getInventory().addItem(productItemStack);
  }
}
