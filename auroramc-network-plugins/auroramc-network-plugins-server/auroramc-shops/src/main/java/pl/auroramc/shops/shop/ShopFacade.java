package pl.auroramc.shops.shop;

import java.nio.file.Path;
import java.util.Set;

public interface ShopFacade {

  static ShopFacade getShopFacade(final Path shopsPath, final ClassLoader pluginClassLoader) {
    ShopService shopService = new ShopService(pluginClassLoader);
    shopService.discoverShopDefinitions(shopsPath);
    return shopService;
  }

  Set<Shop> getShops();
}
