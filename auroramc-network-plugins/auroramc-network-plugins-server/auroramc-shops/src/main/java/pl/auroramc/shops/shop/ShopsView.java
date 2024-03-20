package pl.auroramc.shops.shop;

import static pl.auroramc.commons.page.navigation.PageNavigationDirection.BACKWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationDirection.FORWARD;
import static pl.auroramc.commons.page.navigation.PageNavigationUtils.navigate;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.List;
import java.util.Set;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.shops.product.ProductFacade;

class ShopsView {

  private final Plugin plugin;
  private final ShopFacade shopFacade;
  private final ProductFacade productFacade;
  public ChestGui shopsGui;
  public PaginatedPane shopItemsPane;

  ShopsView(final Plugin plugin, final ShopFacade shopFacade, final ProductFacade productFacade) {
    this.plugin = plugin;
    this.shopFacade = shopFacade;
    this.productFacade = productFacade;
  }

  public void populateShopItems(final PaginatedPane requestingPane) {
    shopItemsPane = requestingPane;
    shopItemsPane.clear();
    shopItemsPane.populateWithGuiItems(getShopItems(shopFacade.getShops()));
    shopsGui.update();
  }

  public void navigateToShop(final InventoryClickEvent event, final Shop shop) {
    productFacade.showProducts(event.getWhoClicked(), shop, shopsGui);
  }

  @Internal
  public void navigateToNextPage() {
    navigate(FORWARD, shopsGui, shopItemsPane);
  }

  @Internal
  public void navigateToPrevPage() {
    navigate(BACKWARD, shopsGui, shopItemsPane);
  }

  @Internal
  public void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  private GuiItem getShopItem(final Shop shop) {
    return new GuiItem(shop.icon(), event -> navigateToShop(event, shop), plugin);
  }

  private List<GuiItem> getShopItems(final Set<Shop> shops) {
    return shops.stream().map(this::getShopItem).toList();
  }
}
