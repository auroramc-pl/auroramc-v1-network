package pl.auroramc.shops.shop;

import static groovy.lang.Closure.DELEGATE_ONLY;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.ArrayList;
import java.util.List;
import pl.auroramc.shops.product.Product;
import pl.auroramc.shops.product.ProductBuilder;

class ShopDsl {

  private ShopDsl() {

  }

  public static Shop shop(
      final @DelegatesTo(value = ShopBuilder.class, strategy = DELEGATE_ONLY) Closure<?> closure
  ) {
    final ShopBuilder shopBuilder = Shop.newBuilder();
    closure.setDelegate(shopBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return shopBuilder.build();
  }

  static class ProductsDsl {

    private final List<Product> products;

    ProductsDsl() {
      this.products = new ArrayList<>();
    }

    public void product(
        final @DelegatesTo(ProductBuilder.class) Closure<?> closure
    ) {
      final ProductBuilder delegate = Product.newBuilder();
      closure.setDelegate(delegate);
      closure.setResolveStrategy(DELEGATE_ONLY);
      closure.call();
      products.add(delegate.build());
    }

    public void product(final Product product) {
      products.add(product);
    }

    List<Product> products() {
      return products;
    }
  }
}