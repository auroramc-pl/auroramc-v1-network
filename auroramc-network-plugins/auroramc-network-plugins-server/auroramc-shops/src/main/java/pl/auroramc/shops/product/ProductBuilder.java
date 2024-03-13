package pl.auroramc.shops.product;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.bukkit.Material.STONE;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.math.BigDecimal;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.item.ItemStackBuilder;

public final class ProductBuilder {

  private ItemStack icon;
  private ItemStack subject;
  private Integer quantity;
  private BigDecimal priceForSale;
  private BigDecimal priceForPurchase;

  ProductBuilder() {

  }

  public ProductBuilder icon(final ItemStack icon) {
    this.icon = icon;
    return this;
  }

  public ProductBuilder icon(
      final @DelegatesTo(value = ItemStackBuilder.class) Closure<?> closure
  ) {
    final ItemStackBuilder itemStackBuilder = ItemStackBuilder.newBuilder(STONE);
    closure.setDelegate(itemStackBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return icon(itemStackBuilder.build());
  }

  public ProductBuilder subject(final ItemStack subject) {
    this.subject = subject;
    return this;
  }

  public ProductBuilder subject(
      final @DelegatesTo(value = ItemStackBuilder.class) Closure<?> closure
  ) {
    final ItemStackBuilder itemStackBuilder = ItemStackBuilder.newBuilder(STONE);
    closure.setDelegate(itemStackBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return subject(itemStackBuilder.build());
  }

  public ProductBuilder quantity(final Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public ProductBuilder priceForSale(final BigDecimal priceForSale) {
    this.priceForSale = priceForSale;
    return this;
  }

  public ProductBuilder priceForPurchase(final BigDecimal priceForPurchase) {
    this.priceForPurchase = priceForPurchase;
    return this;
  }

  public Product build() {
    return new Product(icon, subject, quantity, priceForSale, priceForPurchase);
  }
}
