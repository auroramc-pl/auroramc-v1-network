package pl.auroramc.spawners.spawner;

import static org.bukkit.Material.SPAWNER;
import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.persistence.PersistentDataType.STRING;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpawnerPlaceListener implements Listener {

  private final NamespacedKey spawnedCreatureKey;

  public SpawnerPlaceListener(final NamespacedKey spawnedCreatureKey) {
    this.spawnedCreatureKey = spawnedCreatureKey;
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onSpawnerPlace(final BlockPlaceEvent event) {
    final Block block = event.getBlock();
    if (block.getType() != SPAWNER) {
      return;
    }

    final ItemStack heldItem = event.getItemInHand();
    final ItemMeta itemMeta = heldItem.getItemMeta();
    if (itemMeta.getPersistentDataContainer().has(spawnedCreatureKey)) {
      final EntityType creatureType =
          EntityType.valueOf(itemMeta.getPersistentDataContainer().get(spawnedCreatureKey, STRING));
      if (block.getState() instanceof CreatureSpawner spawner) {
        spawner.setSpawnedType(creatureType);
        spawner.update();
      }
    }
  }
}
