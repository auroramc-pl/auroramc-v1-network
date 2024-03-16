package pl.auroramc.shops.product;

import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationUtils.navigate;
import static pl.auroramc.shops.message.MessageVariableKey.PRICE_VARIABLE_KEY;
import static pl.auroramc.shops.message.MessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.shops.product.ProductViewUtils.mergeLoreOnItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.text.DecimalFormat;
import java.util.List;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.shops.message.MessageSource;
import pl.auroramc.shops.shop.Shop;

class ProductView {

  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final DecimalFormat priceFormat;
  private final ProductFacade productFacade;
  private final Shop shop;
  private final ChestGui shopsGui;
  public ChestGui productGui;
  public PaginatedPane productItemsPane;

  ProductView(
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final DecimalFormat priceFormat,
      final ProductFacade productFacade,
      final Shop shop,
      final ChestGui shopsGui
  ) {
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.priceFormat = priceFormat;
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
      final InventoryClickEvent event, final Product product
  ) {
    if (event.isLeftClick()) {
      productFacade.purchaseProduct(event.getWhoClicked(), product);
    } else if (event.isRightClick()) {
      productFacade.saleProduct(event.getWhoClicked(), product);
    }
  }

  private GuiItem getProductItem(final Product product) {
    final ItemStack originItemStack = product.icon();
    final ItemStack renderItemStack = mergeLoreOnItemStack(
        originItemStack, getAdditionalLoreForProductItem(product)
    );
    return new GuiItem(renderItemStack,
        event -> requestTransactionFinalization(event, product)
    );
  }

  private List<MutableMessage> getAdditionalLoreForProductItem(final Product product) {
    return List.of(
        messageSource.sellTag
            .with(PRICE_VARIABLE_KEY, priceFormat.format(product.priceForSale()))
            .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol()),
        messageSource.purchaseTag
            .with(PRICE_VARIABLE_KEY, priceFormat.format(product.priceForPurchase()))
            .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol()),
        messageSource.sellSuggestion,
        messageSource.purchaseSuggestion
    );
  }

  private List<GuiItem> getProductItems(final List<Product> products) {
    return products.stream()
        .map(this::getProductItem)
        .toList();
  }
}
