package pl.auroramc.gamble.stake.view;


import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

final class StakeViewUtils {

  private static final int COORDINATE_OFFSET = 1;
  private static final int ROW_LENGTH = 9;

  private StakeViewUtils() {

  }

  static void setItemAsFrame(final Inventory inventory, final ItemStack fill) {
    final int rows = inventory.getSize() / ROW_LENGTH;
    for (int index = 0; index < inventory.getSize(); index++) {
      final int row = index / ROW_LENGTH + COORDINATE_OFFSET;
      final int col = index % ROW_LENGTH + COORDINATE_OFFSET;
      if ((row == 1 || row == rows) || (col == 1 || col == 9)) {
        inventory.setItem(index, fill);
      }
    }
  }

  static int getSlotIndexOf(final int x, final int y) {
    return (x - COORDINATE_OFFSET) + ((y - COORDINATE_OFFSET) * ROW_LENGTH);
  }
}
