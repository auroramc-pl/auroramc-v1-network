package pl.auroramc.shops.shop;

import static pl.auroramc.shops.shop.ShopsViewFactory.produceShopsGui;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.shops.product.ProductFacade;

@Permission("auroramc.shops.shop")
@Command(
    name = "shop",
    aliases = {"shops", "sklep"})
public class ShopCommand {

  private final Plugin plugin;
  private final ShopFacade shopFacade;
  private final ProductFacade productFacade;

  public ShopCommand(
      final Plugin plugin, final ShopFacade shopFacade, final ProductFacade productFacade) {
    this.plugin = plugin;
    this.shopFacade = shopFacade;
    this.productFacade = productFacade;
  }

  @Execute
  public void shop(final @Context Player player) {
    produceShopsGui(plugin, shopFacade, productFacade).show(player);
  }
}
