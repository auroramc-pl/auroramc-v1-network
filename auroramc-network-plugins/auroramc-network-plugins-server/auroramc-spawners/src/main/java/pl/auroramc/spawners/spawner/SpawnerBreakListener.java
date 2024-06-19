package pl.auroramc.spawners.spawner;

import static com.destroystokyo.paper.MaterialTags.PICKAXES;
import static org.bukkit.Material.SPAWNER;
import static org.bukkit.enchantments.Enchantment.SILK_TOUCH;
import static org.bukkit.event.EventPriority.HIGHEST;
import static pl.auroramc.commons.random.RandomUtils.randomEvent;
import static pl.auroramc.integrations.item.ItemStackUtils.giveOrDropItemStack;
import static pl.auroramc.spawners.spawner.SpawnerUtils.getSpawnerItem;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.spawners.SpawnersConfig;

public class SpawnerBreakListener implements Listener {

  private final NamespacedKey spawnedCreatureKey;
  private final SpawnersConfig spawnersConfig;
  private final SpawnerMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  public SpawnerBreakListener(
      final NamespacedKey spawnedCreatureKey,
      final SpawnersConfig spawnersConfig,
      final SpawnerMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.spawnedCreatureKey = spawnedCreatureKey;
    this.spawnersConfig = spawnersConfig;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onSpawnerBreak(final BlockBreakEvent event) {
    final Block block = event.getBlock();
    if (block.getType() != SPAWNER) {
      return;
    }

    final Player player = event.getPlayer();
    if (block.getState() instanceof CreatureSpawner spawner
        && isRelevantItem(player.getInventory().getItemInMainHand())) {
      event.setDropItems(false);
      event.setExpToDrop(0);
      if (randomEvent(spawnersConfig.spawnerReacquirePercentage)) {
        giveOrDropItemStack(
            player,
            getSpawnerItem(
                spawnedCreatureKey, messageSource, messageCompiler, spawner.getSpawnedType()));
      }
    }
  }

  private boolean isRelevantItem(final ItemStack itemStack) {
    return PICKAXES.isTagged(itemStack) && itemStack.getEnchantmentLevel(SILK_TOUCH) > 0;
  }
}
