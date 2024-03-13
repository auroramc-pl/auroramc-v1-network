package shops

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import pl.auroramc.shops.product.Product

import static pl.auroramc.shops.shop.ShopDsl.shop
import static shops.HopperUtils.getHopperWithTransferQuantity

shop {
  icon {
    displayName("<gray>Bloki")
  }
  paymentCurrencyId(1)
  products {
    product {
      icon {
        displayName("<gray>Kamień")
      }
      subject {
        type(Material.STONE)
      }
      quantity(64)
      priceForPurchase(BigDecimal.TEN)
      priceForSale(BigDecimal.ONE)
    }
    product(getHopperWithTransferQuantity(2, new BigDecimal(150), new BigDecimal(100)))
    product(getHopperWithTransferQuantity(4, new BigDecimal(300), new BigDecimal(150)))
    product(getHopperWithTransferQuantity(8, new BigDecimal(500), new BigDecimal(275)))
  }
}

class HopperUtils {

  static def getHopperWithTransferQuantity(
      final int transferQuantity = 1,
      final BigDecimal suggestedPurchasePrice,
      final BigDecimal suggestedSalePrice) {
    return Product.newBuilder()
        .with {
          icon {
            type(Material.HOPPER)
            displayName("<gray>Hopper <dark_gray>(<white>$transferQuantity</white><dark_gray>)")
          }
          subject {
            type(Material.HOPPER)
            data(new NamespacedKey("auroramc-hoppers", "transfer_quantity"), PersistentDataType.INTEGER, transferQuantity)
            displayName("<gray>Hopper <dark_gray>(<white>$transferQuantity</white><dark_gray>)")
          }
          quantity(1)
          priceForPurchase(suggestedPurchasePrice)
          priceForSale(suggestedSalePrice)
        }
        .build()
  }
}