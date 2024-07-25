package shops

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import pl.auroramc.shops.product.Product

import static pl.auroramc.shops.shop.ShopDsl.shop
import static shops.HopperUtils.getHopperWithTransferQuantity

shop {
    icon {
        lore("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Bloki")
    }
    paymentCurrencyId(1)
    products {
        product {
            icon {
                displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Kamień")
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
                        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Hopper <#d3a37e>(<gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78>$transferQuantity<#d3a37e>)")
                    }
                    subject {
                        type(Material.HOPPER)
                        data(new NamespacedKey("auroramc-hoppers", "transfer_quantity"), PersistentDataType.INTEGER, transferQuantity)
                        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Hopper <#d3a37e>(<gradient:#f7cf78:#fbd06a:#f4b352:#fbd06a:#f7cf78>$transferQuantity<#d3a37e>)")
                    }
                    quantity(1)
                    priceForPurchase(suggestedPurchasePrice)
                    priceForSale(suggestedSalePrice)
                }
                .build()
    }
}