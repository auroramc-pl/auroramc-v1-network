package pl.auroramc.hoppers.hopper;

import static org.bukkit.Material.HOPPER;
import static org.bukkit.persistence.PersistentDataType.INTEGER;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class HopperInitializeListener implements Listener {

  private final Plugin plugin;
  private final NamespacedKey transferQuantityKey;

  public HopperInitializeListener(final Plugin plugin, final NamespacedKey transferQuantityKey) {
    this.plugin = plugin;
    this.transferQuantityKey = transferQuantityKey;
  }

  @EventHandler
  public void onHopperInitialize(final BlockPlaceEvent event) {
    final ItemStack itemInHand = event.getItemInHand();
    if (whetherBlockIsHopper(event.getBlock()) && whetherBlockHasTransferQuantity(itemInHand)) {
      final CustomBlockData blockData = new CustomBlockData(event.getBlock(), plugin);
      blockData.set(transferQuantityKey, INTEGER, resolveTransferQuantity(itemInHand));
    }
  }

  private boolean whetherBlockIsHopper(final Block block) {
    return block.getType() == HOPPER;
  }

  private boolean whetherBlockHasTransferQuantity(final ItemStack itemStack) {
    return itemStack.getItemMeta()
        .getPersistentDataContainer()
        .has(transferQuantityKey, INTEGER);
  }

  private Integer resolveTransferQuantity(final ItemStack itemStack) {
    return itemStack.getItemMeta()
        .getPersistentDataContainer()
        .get(transferQuantityKey, INTEGER);
  }
}
