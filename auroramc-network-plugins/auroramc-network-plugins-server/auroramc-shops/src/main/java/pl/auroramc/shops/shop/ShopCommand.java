package pl.auroramc.shops.shop;

import static pl.auroramc.shops.shop.ShopsViewFactory.produceShopsGui;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.shops.product.ProductFacade;

@Permission("auroramc.shops.shop")
@Route(name = "shop", aliases = {"shops", "sklep", "sklepy"})
public class ShopCommand {

  private final Plugin plugin;
  private final ShopFacade shopFacade;
  private final ProductFacade productFacade;

  public ShopCommand(
      final Plugin plugin, final ShopFacade shopFacade, final ProductFacade productFacade
  ) {
    this.plugin = plugin;
    this.shopFacade = shopFacade;
    this.productFacade = productFacade;
  }

  @Execute
  public void shop(final Player player) {
    produceShopsGui(plugin, shopFacade, productFacade).show(player);
  }
}
