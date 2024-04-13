package pl.auroramc.shops.product;

import static pl.auroramc.commons.bukkit.item.ItemStackUtils.getItemStackWithQuantity;
import static pl.auroramc.commons.bukkit.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.bukkit.page.navigation.PageNavigationUtils.navigate;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;
import static pl.auroramc.shops.product.ProductMessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.shops.product.ProductViewUtils.mergeLoreOnItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.shops.shop.Shop;

class ProductView {

  private final Plugin plugin;
  private final Currency fundsCurrency;
  private final ProductMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final ProductFacade productFacade;
  private final Shop shop;
  private final ChestGui shopsGui;
  public ChestGui productGui;
  public PaginatedPane productItemsPane;

  ProductView(
      final Plugin plugin,
      final Currency fundsCurrency,
      final ProductMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final ProductFacade productFacade,
      final Shop shop,
      final ChestGui shopsGui) {
    this.plugin = plugin;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.productFacade = productFacade;
    this.shop = shop;
    this.shopsGui = shopsGui;
  }

  public void populateProductItems(final PaginatedPane requestingPane) {
    productItemsPane = requestingPane;
    productItemsPane.clear();
    productItemsPane.populateWithGuiItems(getProductItems(shop.products()));
    productGui.update();
  }

  public void navigateToShops(final InventoryClickEvent event) {
    shopsGui.show(event.getWhoClicked());
  }

  @Internal
  public void navigateToNextPage() {
    navigate(FORWARD, productGui, productItemsPane);
  }

  @Internal
  public void navigateToPrevPage() {
    navigate(BACKWARD, productGui, productItemsPane);
  }

  @Internal
  public void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public void requestTransactionFinalization(
      final InventoryClickEvent event, final Product product) {
    if (event.isLeftClick()) {
      productFacade.purchaseProduct((Player) event.getWhoClicked(), product);
    } else if (event.isRightClick()) {
      productFacade.saleProduct((Player) event.getWhoClicked(), product);
    }
  }

  private GuiItem getProductItem(final Product product) {
    final ItemStack originItemStack = product.icon();
    final ItemStack renderItemStack =
        mergeLoreOnItemStack(originItemStack, getAdditionalLoreForProductItem(product));
    return new GuiItem(
        renderItemStack, event -> requestTransactionFinalization(event, product), plugin);
  }

  private List<CompiledMessage> getAdditionalLoreForProductItem(final Product product) {
    return Stream.of(
            messageSource.sellTag.placeholder(
                CONTEXT_PATH,
                new ProductContext(
                    getItemStackWithQuantity(product.subject(), product.quantity()),
                    fundsCurrency,
                    product.priceForSale())),
            messageSource.purchaseTag.placeholder(
                CONTEXT_PATH,
                new ProductContext(
                    getItemStackWithQuantity(product.subject(), product.quantity()),
                    fundsCurrency,
                    product.priceForPurchase())),
            messageSource.sellSuggestion,
            messageSource.purchaseSuggestion)
        .map(message -> messageCompiler.compile(message, NO_CURSIVE))
        .toList();
  }

  private List<GuiItem> getProductItems(final List<Product> products) {
    return products.stream().map(this::getProductItem).toList();
  }
}
