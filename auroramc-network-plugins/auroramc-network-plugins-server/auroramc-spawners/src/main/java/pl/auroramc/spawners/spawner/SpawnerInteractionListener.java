package pl.auroramc.spawners.spawner;

import static org.bukkit.Material.SPAWNER;
import static org.bukkit.event.EventPriority.HIGHEST;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static pl.auroramc.spawners.spawner.SpawnersViewFactory.getSpawnersGui;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class SpawnerInteractionListener implements Listener {

  private final Plugin plugin;
  private final Currency fundsCurrency;
  private final SpawnerFacade spawnerFacade;
  private final SpawnerController spawnerController;
  private final SpawnerMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  public SpawnerInteractionListener(
      final Plugin plugin,
      final Currency fundsCurrency,
      final SpawnerFacade spawnerFacade,
      final SpawnerController spawnerController,
      final SpawnerMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.plugin = plugin;
    this.fundsCurrency = fundsCurrency;
    this.spawnerFacade = spawnerFacade;
    this.spawnerController = spawnerController;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  @EventHandler(priority = HIGHEST, ignoreCancelled = true)
  public void onSpawnerInteraction(final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    if (event.getAction() != RIGHT_CLICK_BLOCK || !player.isSneaking()) {
      return;
    }

    final Block block = event.getClickedBlock();
    if (block == null || block.getType() != SPAWNER) {
      return;
    }

    if (block.getState() instanceof CreatureSpawner spawner) {
      processSpawnerInteraction(player, spawner);
    }
  }

  private void processSpawnerInteraction(final Player player, final CreatureSpawner spawner) {
    getSpawnersGui(
            plugin,
            fundsCurrency,
            spawnerFacade,
            spawnerController,
            messageSource,
            messageCompiler,
            spawner)
        .show(player);
  }
}
