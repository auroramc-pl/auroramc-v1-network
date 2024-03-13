package pl.auroramc.shops.shop;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.shops.product.Product;

public record Shop(Long paymentCurrencyId, ItemStack icon, List<Product> products) {

  public static ShopBuilder newBuilder() {
    return new ShopBuilder();
  }
}
