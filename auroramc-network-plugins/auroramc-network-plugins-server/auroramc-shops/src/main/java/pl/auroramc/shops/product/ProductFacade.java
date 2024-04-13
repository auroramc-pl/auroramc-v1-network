package pl.auroramc.shops.product;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.shops.shop.Shop;

public interface ProductFacade {

  static ProductFacade getProductFacade(
      final Plugin plugin,
      final Scheduler scheduler,
      final ProductMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade) {
    return new ProductService(
        plugin, scheduler, messageSource, messageCompiler, fundsCurrency, economyFacade);
  }

  void showProducts(final Player player, final Shop shop, final ChestGui shopsGui);

  void saleProduct(final Player player, final Product product);

  void purchaseProduct(final Player player, final Product product);
}
