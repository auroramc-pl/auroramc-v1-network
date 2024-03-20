package pl.auroramc.shops.product;

import static java.lang.Math.ceil;
import static java.util.Arrays.stream;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

final class ProductUtils {

  private ProductUtils() {}

  static int getQuantityInSlots(final int quantity, final double maximumStackSize) {
    return (int) ceil(quantity / maximumStackSize);
  }

  static int getEmptySlotsCount(final Inventory inventory, final ItemStack query) {
    return (int)
        stream(inventory.getStorageContents())
            .filter(itemStack -> whetherSlotIsAvailable(itemStack, query))
            .count();
  }

  private static boolean whetherSlotIsAvailable(final ItemStack itemStack, final ItemStack query) {
    return itemStack == null
        || (itemStack.isSimilar(query) && itemStack.getAmount() < itemStack.getMaxStackSize());
  }
}
