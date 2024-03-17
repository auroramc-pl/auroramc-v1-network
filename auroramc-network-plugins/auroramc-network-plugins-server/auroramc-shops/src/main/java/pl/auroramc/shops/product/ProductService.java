package pl.auroramc.shops.product;

import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;
import static pl.auroramc.shops.message.MessageVariableKey.AMOUNT_VARIABLE_KEY;
import static pl.auroramc.shops.message.MessageVariableKey.PRODUCT_VARIABLE_KEY;
import static pl.auroramc.shops.message.MessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.shops.product.ProductUtils.getEmptySlotsCount;
import static pl.auroramc.shops.product.ProductUtils.getQuantityInSlots;
import static pl.auroramc.shops.product.ProductViewFactory.produceProductGui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.shops.message.MessageSource;
import pl.auroramc.shops.shop.Shop;

class ProductService implements ProductFacade {

  private final Plugin plugin;
  private final Logger logger;
  private final MessageSource messageSource;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;
  private final DecimalFormat priceFormat;

  ProductService(
      final Plugin plugin,
      final Logger logger,
      final MessageSource messageSource,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade,
      final DecimalFormat priceFormat
  ) {
    this.plugin = plugin;
    this.logger = logger;
    this.messageSource = messageSource;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
    this.priceFormat = priceFormat;
  }

  @Override
  public void showProducts(
      final HumanEntity entity, final Shop shop, final ChestGui shopsGui
  ) {
    produceProductGui(
        plugin, fundsCurrency, messageSource,this, priceFormat, shop, shopsGui
    ).show(entity);
  }

  @Override
  public void saleProduct(final HumanEntity entity, final Product product) {
    if (whetherEntityIsOutOfStock(entity, product)) {
      entity.sendMessage(messageSource.productCouldNotBeSoldBecauseOfMissingStock.compile());
      return;
    }

    economyFacade.deposit(entity.getUniqueId(), fundsCurrency, product.priceForSale())
        .thenAccept(state -> postToMainThread(plugin,
            () ->
                entity.getInventory().removeItemAnySlot(
                    getItemStackWithQuantity(product.subject(), product.quantity())
                )
            )
        )
        .thenApply(state -> getProductMessage(messageSource.productSold, product, product.priceForSale()))
        .thenApply(MutableMessage::compile)
        .thenAccept(entity::sendMessage)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private boolean whetherEntityIsOutOfStock(final HumanEntity entity, final Product product) {
    return !entity
        .getInventory()
        .containsAtLeast(product.subject(), product.quantity());
  }

  @Override
  public void purchaseProduct(final HumanEntity entity, final Product product) {
    economyFacade.has(entity.getUniqueId(), fundsCurrency, product.priceForPurchase())
        .thenCompose(whetherEntityHasEnoughFunds ->
            finalizeProductPurchase(entity, product, whetherEntityHasEnoughFunds)
        )
        .thenApply(MutableMessage::compile)
        .thenAccept(entity::sendMessage)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> finalizeProductPurchase(
      final HumanEntity entity, final Product product, final boolean whetherEntityHasEnoughFunds
  ) {
    if (!whetherEntityHasEnoughFunds) {
      return messageSource.productCouldNotBeBoughtBecauseOfMissingMoney
          .asCompletedFuture();
    }

    final int requiredSlots = getQuantityInSlots(
        product.quantity(),
        product.subject().getMaxStackSize()
    );
    final int obtainedSlots = getEmptySlotsCount(
        entity.getInventory(), product.subject()
    );
    if (requiredSlots > obtainedSlots) {
      return messageSource.productCouldNotBeBoughtBecauseOfMissingSpace
          .asCompletedFuture();
    }

    return economyFacade.withdraw(entity.getUniqueId(), fundsCurrency, product.priceForPurchase())
        .thenAccept(state -> postToMainThread(plugin,
            () ->
                giveItemOrDropIfFull(
                    entity,
                    getItemStackWithQuantity(product.subject(), product.quantity())
                )
            )
        )
        .thenApply(
            state -> getProductMessage(
                messageSource.productBought, product, product.priceForPurchase()
            )
        )
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private MutableMessage getProductMessage(
      final MutableMessage message, final Product product, final BigDecimal merchandiseValue
  ) {
    return message
        .with(
            PRODUCT_VARIABLE_KEY,
            getFormattedItemStack(
                product.subject(),
                product.quantity()
            )
        )
        .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol())
        .with(AMOUNT_VARIABLE_KEY, priceFormat.format(merchandiseValue));
  }

  private void giveItemOrDropIfFull(final HumanEntity entity, final ItemStack itemStack) {
    entity.getInventory().addItem(itemStack)
        .forEach((index, remainingItem) ->
            entity.getLocation()
                .getWorld()
                .dropItemNaturally(entity.getLocation(), remainingItem)
        );
  }

  private ItemStack getItemStackWithQuantity(final ItemStack itemStack, final int quantity) {
    final ItemStack copyOfItemStack = itemStack.clone();
    copyOfItemStack.setAmount(quantity);
    return copyOfItemStack;
  }
}
