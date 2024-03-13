package pl.auroramc.shops.shop;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.bukkit.Material.STONE;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;
import pl.auroramc.shops.product.Product;
import pl.auroramc.shops.shop.ShopDsl.ProductsDsl;

public final class ShopBuilder {

  private Long paymentCurrencyId;
  private ItemStack icon;
  private List<Product> products;

  ShopBuilder() {

  }

  public ShopBuilder paymentCurrencyId(final Long paymentCurrencyId) {
    this.paymentCurrencyId = paymentCurrencyId;
    return this;
  }

  public ShopBuilder icon(final ItemStack icon) {
    this.icon = icon;
    return this;
  }

  public ShopBuilder icon(final @DelegatesTo(value = ItemStackBuilder.class) Closure<?> closure) {
    final ItemStackBuilder itemStackBuilder = ItemStackBuilder.newBuilder(STONE);
    closure.setDelegate(itemStackBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return icon(itemStackBuilder.build());
  }

  public ShopBuilder products(final List<Product> products) {
    this.products = products;
    return this;
  }

  public ShopBuilder products(final @DelegatesTo(value = ProductsDsl.class) Closure<?> closure) {
    final ProductsDsl productsDsl = new ProductsDsl();
    closure.setDelegate(productsDsl);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return products(productsDsl.products());
  }

  public ShopBuilder products(final Product... products) {
    return products(List.of(products));
  }

  public Shop build() {
    return new Shop(paymentCurrencyId, icon, products);
  }
}
