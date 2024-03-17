package pl.auroramc.shops.product;

import static com.github.stefvanschie.inventoryframework.gui.type.ChestGui.load;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import org.bukkit.plugin.Plugin;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.shops.message.MessageSource;
import pl.auroramc.shops.shop.Shop;

final class ProductViewFactory {

  private static final String VIEW_DEFINITION_RESOURCE_PATH = "guis/product.xml";

  private ProductViewFactory() {

  }

  static ChestGui produceProductGui(
      final Plugin plugin,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final ProductFacade productFacade,
      final DecimalFormat priceFormat,
      final Shop shop,
      final ChestGui shopsGui
  ) {
    try (final InputStream inputStream = plugin.getResource(VIEW_DEFINITION_RESOURCE_PATH)) {
      if (inputStream == null) {
        throw new ProductViewInstantiationException(
            "Could not find product gui definition in resources."
        );
      }

      return load(
          new ProductView(
              plugin,
              fundsCurrency,
              messageSource,
              priceFormat,
              productFacade,
              shop,
              shopsGui
          ),
          inputStream,
          plugin
      );
    } catch (final IOException exception) {
      throw new ProductViewInstantiationException(
          "Could not load product gui from resources, because of unexpected exception.",
          exception
      );
    }
  }
}
