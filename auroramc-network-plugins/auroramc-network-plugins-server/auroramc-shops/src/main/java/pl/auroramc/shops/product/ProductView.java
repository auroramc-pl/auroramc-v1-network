package pl.auroramc.shops.product;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationUtils.navigate;
import static pl.auroramc.shops.product.ProductViewUtils.mergeLoreOnItemStack;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.text.DecimalFormat;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.shops.shop.Shop;

class ProductView {

  private final Currency fundsCurrency;
  private final DecimalFormat priceFormat;
  private final ProductFacade productFacade;
  private final Shop shop;
  private final ChestGui shopsGui;
  public ChestGui productGui;
  public PaginatedPane productItemsPane;

  ProductView(
      final Currency fundsCurrency,
      final DecimalFormat priceFormat,
      final ProductFacade productFacade,
      final Shop shop,
      final ChestGui shopsGui
  ) {
    this.fundsCurrency = fundsCurrency;
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
    }
    else if (event.isRightClick()) {
      productFacade.saleProduct(event.getWhoClicked(), product);
    }
  }

  private GuiItem getProductItem(final Product product) {
    final ItemStack originItemStack = product.icon();
    final ItemStack renderItemStack = mergeLoreOnItemStack(
        originItemStack, getAdditionalLoreForProductItem(product)
    );
    return new GuiItem(renderItemStack, event -> requestTransactionFinalization(event, product));
  }

  private List<Component> getAdditionalLoreForProductItem(final Product product) {
    return List.of(
        miniMessage().deserialize(
            "<gray>Cena zakupu: <white><price_symbol><price_for_purchase>",
            getProductItemTagResolvers(product)
        ),
        miniMessage().deserialize(
            "<gray>Cena sprzedaży: <white><price_symbol><price_for_sale>",
            getProductItemTagResolvers(product)
        ),
        miniMessage().deserialize(
            "<gray>Naciśnij <white>LPM <gray>aby zakupić ten przedmiot."
        ),
        miniMessage().deserialize(
            "<gray>Naciśnij <white>PPM <gray>aby sprzedać ten przedmiot."
        )
    );
  }

  private TagResolver[] getProductItemTagResolvers(final Product product) {
    return List.of(
        Placeholder.unparsed("price_symbol", fundsCurrency.getSymbol()),
        Placeholder.unparsed("price_for_sale", priceFormat.format(product.priceForSale())),
        Placeholder.unparsed("price_for_purchase", priceFormat.format(product.priceForPurchase()))
    ).toArray(TagResolver[]::new);
  }

  private List<GuiItem> getProductItems(final List<Product> products) {
    return products.stream()
        .map(this::getProductItem)
        .toList();
  }
}
