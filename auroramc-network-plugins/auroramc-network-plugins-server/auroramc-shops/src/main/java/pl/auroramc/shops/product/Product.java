package pl.auroramc.shops.product;

import java.math.BigDecimal;
import org.bukkit.inventory.ItemStack;

public record Product(
    ItemStack icon,
    ItemStack subject,
    Integer quantity,
    BigDecimal priceForSale,
    BigDecimal priceForPurchase
) {

  public static ProductBuilder newBuilder() {
    return new ProductBuilder();
  }
}
