package pl.auroramc.shops.product;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.bukkit.item.ItemStackUtils.getItemStackWithQuantity;
import static pl.auroramc.commons.bukkit.item.ItemStackUtils.giveOrDropItemStack;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.shops.product.ProductMessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.shops.product.ProductUtils.getEmptySlotsCount;
import static pl.auroramc.shops.product.ProductUtils.getQuantityInSlots;
import static pl.auroramc.shops.product.ProductViewFactory.produceProductGui;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.Viewer;
import pl.auroramc.shops.shop.Shop;

class ProductService implements ProductFacade {

  private final Plugin plugin;
  private final Scheduler scheduler;
  private final ProductMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final Currency fundsCurrency;
  private final EconomyFacade economyFacade;

  ProductService(
      final Plugin plugin,
      final Scheduler scheduler,
      final ProductMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade) {
    this.plugin = plugin;
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.fundsCurrency = fundsCurrency;
    this.economyFacade = economyFacade;
  }

  @Override
  public void showProducts(final Player player, final Shop shop, final ChestGui shopsGui) {
    produceProductGui(plugin, fundsCurrency, messageSource, messageCompiler, this, shop, shopsGui)
        .show(player);
  }

  @Override
  public void saleProduct(final Player player, final Product product) {
    final Viewer viewer = BukkitViewer.wrap(player);
    if (whetherEntityIsOutOfStock(player, product)) {
      viewer.deliver(
          messageCompiler.compile(messageSource.productCouldNotBeSoldBecauseOfMissingStock));
      return;
    }

    economyFacade
        .deposit(player.getUniqueId(), fundsCurrency, product.priceForSale())
        .thenAccept(
            state ->
                scheduler.run(
                    SYNC,
                    () ->
                        player
                            .getInventory()
                            .removeItemAnySlot(
                                getItemStackWithQuantity(product.subject(), product.quantity()))))
        .thenApply(
            state -> getProductMessage(messageSource.productSold, product, product.priceForSale()))
        .thenAccept(viewer::deliver)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public void purchaseProduct(final Player player, final Product product) {
    final Viewer viewer = BukkitViewer.wrap(player);
    economyFacade
        .has(player.getUniqueId(), fundsCurrency, product.priceForPurchase())
        .thenCompose(
            whetherEntityHasEnoughFunds ->
                finalizeProductPurchase(player, product, whetherEntityHasEnoughFunds))
        .thenAccept(viewer::deliver)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private boolean whetherEntityIsOutOfStock(final Player entity, final Product product) {
    return !entity.getInventory().containsAtLeast(product.subject(), product.quantity());
  }

  private CompletableFuture<CompiledMessage> finalizeProductPurchase(
      final Player player, final Product product, final boolean whetherEntityHasEnoughFunds) {
    if (!whetherEntityHasEnoughFunds) {
      return completedFuture(
          messageCompiler.compile(messageSource.productCouldNotBeBoughtBecauseOfMissingMoney));
    }

    final int requiredSlots =
        getQuantityInSlots(product.quantity(), product.subject().getMaxStackSize());
    final int obtainedSlots = getEmptySlotsCount(player.getInventory(), product.subject());
    if (requiredSlots > obtainedSlots) {
      return completedFuture(
          messageCompiler.compile(messageSource.productCouldNotBeBoughtBecauseOfMissingSpace));
    }

    return economyFacade
        .withdraw(player.getUniqueId(), fundsCurrency, product.priceForPurchase())
        .thenAccept(
            state ->
                scheduler.run(
                    SYNC,
                    () ->
                        giveOrDropItemStack(
                            player,
                            getItemStackWithQuantity(product.subject(), product.quantity()))))
        .thenApply(
            state ->
                getProductMessage(messageSource.productBought, product, product.priceForPurchase()))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompiledMessage getProductMessage(
      final MutableMessage message, final Product product, final BigDecimal merchandiseValue) {
    return messageCompiler.compile(
        message.placeholder(
            CONTEXT_PATH,
            new ProductContext(
                getItemStackWithQuantity(product.subject(), product.quantity()),
                fundsCurrency,
                merchandiseValue)));
  }
}
