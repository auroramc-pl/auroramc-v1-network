package pl.auroramc.hoppers.hopper;

import static java.time.Duration.ofSeconds;
import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.inventory.InventoryType.HOPPER;
import static org.bukkit.persistence.PersistentDataType.INTEGER;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jeff_media.customblockdata.CustomBlockData;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

public class HopperTransferListener implements Listener {

  private static final int DEFAULT_TRANSFER_QUANTITY = 1;
  private final Plugin plugin;
  private final Scheduler scheduler;
  private final NamespacedKey transferQuantityKey;
  private final Cache<Position, Integer> transferQuantityByPositionCache;

  public HopperTransferListener(
      final Plugin plugin, final Scheduler scheduler, final NamespacedKey transferQuantityKey) {
    this.plugin = plugin;
    this.scheduler = scheduler;
    this.transferQuantityKey = transferQuantityKey;
    this.transferQuantityByPositionCache =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(20))
            .build();
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onHopperMoveItem(final InventoryMoveItemEvent event) {
    final Inventory initiator = event.getInitiator();
    if (whetherIsNotInitiatedByHopper(initiator)) {
      return;
    }

    final Inventory sourceInventory = event.getSource();
    final Inventory targetInventory = event.getDestination();

    final int transferringQuantity = getTransferQuantity(initiator.getLocation());
    final ItemStack transferredItemStack = event.getItem().clone();
    transferredItemStack.setAmount(transferringQuantity);

    scheduler.run(
        SYNC,
        () -> {
          sourceInventory.removeItemAnySlot(transferredItemStack);
          targetInventory.addItem(transferredItemStack);
        });

    event.setCancelled(true);
  }

  private boolean whetherIsNotInitiatedByHopper(final Inventory initiator) {
    return initiator.getType() != HOPPER;
  }

  private int getTransferQuantity(final Location location) {
    final Position position =
        Optional.ofNullable(location)
            .map(
                value ->
                    new Position(
                        value.getWorld().getName(),
                        value.getBlockX(),
                        value.getBlockY(),
                        value.getBlockZ()))
            .orElseThrow(
                () -> new HopperTransferringException("Could not retrieve initiator's location."));

    return transferQuantityByPositionCache.get(
        position,
        key -> getTransferQuantityBlocking(new CustomBlockData(location.getBlock(), plugin)));
  }

  private int getTransferQuantityBlocking(final CustomBlockData blockData) {
    final int transferQuantity =
        Optional.ofNullable(blockData.get(transferQuantityKey, INTEGER))
            .orElse(DEFAULT_TRANSFER_QUANTITY);
    if (transferQuantity <= 0) {
      throw new HopperTransferringException("Hopper transfer quantity is less than or equal to 0.");
    }
    return transferQuantity;
  }

  private record Position(String dimensionName, int x, int y, int z) {}
}
