package pl.auroramc.shops.shop;

import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.BACKWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationDirection.FORWARD;
import static pl.auroramc.commons.bukkit.page.navigation.NavigationUtils.navigate;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.External;
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

  public @External void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public @External void populateShopItems(final PaginatedPane requestingPane) {
    shopItemsPane = requestingPane;
    shopItemsPane.clear();
    shopItemsPane.populateWithGuiItems(getShopItems(shopFacade.getShops()));
    shopsGui.update();
  }

  public @External void navigateToNextPage() {
    navigate(FORWARD, shopsGui, shopItemsPane);
  }

  public @External void navigateToPrevPage() {
    navigate(BACKWARD, shopsGui, shopItemsPane);
  }

  public void navigateToShop(final InventoryClickEvent event, final Shop shop) {
    productFacade.showProducts((Player) event.getWhoClicked(), shop, shopsGui);
  }

  private GuiItem getShopItem(final Shop shop) {
    return new GuiItem(shop.icon(), event -> navigateToShop(event, shop), plugin);
  }

  private List<GuiItem> getShopItems(final Set<Shop> shops) {
    return shops.stream().map(this::getShopItem).toList();
  }
}
