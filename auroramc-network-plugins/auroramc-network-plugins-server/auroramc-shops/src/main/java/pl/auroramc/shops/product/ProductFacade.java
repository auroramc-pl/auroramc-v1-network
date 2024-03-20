package pl.auroramc.shops.product;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.text.DecimalFormat;
import java.util.logging.Logger;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.shops.message.MessageSource;
import pl.auroramc.shops.shop.Shop;

public interface ProductFacade {

  static ProductFacade getProductFacade(
      final Plugin plugin,
      final Logger logger,
      final MessageSource messageSource,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade,
      final DecimalFormat priceFormat) {
    return new ProductService(
        plugin, logger, messageSource, fundsCurrency, economyFacade, priceFormat);
  }

  void showProducts(final HumanEntity view, final Shop shop, final ChestGui shopsGui);

  void saleProduct(final HumanEntity entity, final Product product);

  void purchaseProduct(final HumanEntity entity, final Product product);
}
