package pl.auroramc.shops.shop;

import static com.github.stefvanschie.inventoryframework.gui.type.ChestGui.load;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.plugin.Plugin;
import pl.auroramc.shops.product.ProductFacade;

final class ShopsViewFactory {

  private static final String VIEW_DEFINITION_RESOURCE_PATH = "guis/shops.xml";

  private ShopsViewFactory() {

  }

  static ChestGui produceShopsGui(
      final Plugin plugin, final ShopFacade shopFacade, final ProductFacade productFacade
  ) {
    try (final InputStream inputStream = plugin.getResource(VIEW_DEFINITION_RESOURCE_PATH)) {
      if (inputStream == null) {
        throw new ShopsViewInstantiationException(
            "Could not find shops gui definition in resources."
        );
      }

      return load(new ShopsView(shopFacade, productFacade), inputStream, plugin);
    } catch (final IOException exception) {
      throw new ShopsViewInstantiationException(
          "Could not load shops gui from resources, because of unexpected exception.",
          exception
      );
    }
  }
}
