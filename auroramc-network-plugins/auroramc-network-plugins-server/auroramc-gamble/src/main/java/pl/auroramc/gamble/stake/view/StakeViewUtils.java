package pl.auroramc.gamble.stake.view;

import static java.lang.Math.min;
import static java.util.stream.IntStream.iterate;

import java.util.List;
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

  static <T> List<List<T>> partition(final List<T> items, final int partitionSize) {
    return iterate(0, index -> index + partitionSize)
        .limit((long) Math.ceil((double) items.size() / partitionSize))
        .mapToObj(i -> items.subList(i, min(i + partitionSize, items.size())))
        .toList();
  }
}
