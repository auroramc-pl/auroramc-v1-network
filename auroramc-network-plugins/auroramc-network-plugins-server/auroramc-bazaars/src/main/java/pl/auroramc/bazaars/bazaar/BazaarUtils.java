package pl.auroramc.bazaars.bazaar;

import static java.util.Arrays.stream;
import static org.bukkit.Material.CHEST;
import static org.bukkit.Material.TRAPPED_CHEST;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class BazaarUtils {

  private BazaarUtils() {}

  public static int getQuantityInSlots(final int quantity, final double maximumStackSize) {
    return (int) Math.ceil(quantity / maximumStackSize);
  }

  public static int getEmptySlotsCount(final Inventory inventory, final Material query) {
    return (int)
        stream(inventory.getStorageContents())
            .filter(itemStack -> whetherSlotIsAvailable(itemStack, query))
            .count();
  }

  public static Block resolveSignProp(final Sign sign, final WallSign wallSign) {
    return sign.getBlock().getRelative(wallSign.getFacing().getOppositeFace());
  }

  public static boolean whetherSignHasInvalidProp(final Block attachedBlock) {
    return attachedBlock.getType() != CHEST && attachedBlock.getType() != TRAPPED_CHEST;
  }

  private static boolean whetherSlotIsAvailable(final ItemStack itemStack, final Material query) {
    return itemStack == null
        || (itemStack.getType() == query && itemStack.getAmount() < itemStack.getMaxStackSize());
  }
}
