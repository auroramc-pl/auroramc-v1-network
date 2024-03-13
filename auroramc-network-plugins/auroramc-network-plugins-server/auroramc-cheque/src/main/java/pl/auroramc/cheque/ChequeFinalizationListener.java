package pl.auroramc.cheque;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;
import static pl.auroramc.commons.BukkitUtils.postToMainThreadAndNextTick;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.message.MutableMessage;

class ChequeFinalizationListener implements Listener {

  private final Plugin plugin;
  private final Logger logger;
  private final ChequeFacade chequeFacade;

  ChequeFinalizationListener(final Plugin plugin, final Logger logger, final ChequeFacade chequeFacade) {
    this.plugin = plugin;
    this.logger = logger;
    this.chequeFacade = chequeFacade;
  }

  @EventHandler
  public void onChequeFinalization(final PlayerInteractEvent event) {
    if (event.getHand() == OFF_HAND) {
      return;
    }

    if (event.getAction() != RIGHT_CLICK_AIR && event.getAction() != RIGHT_CLICK_BLOCK) {
      return;
    }

    final ItemStack heldItemStack = event.getItem();
    if (heldItemStack == null) {
      return;
    }

    if (chequeFacade.whetherItemIsCheque(heldItemStack.clone())) {
      final Player retriever = event.getPlayer();
      event.setCancelled(true);
      postToMainThreadAndNextTick(plugin,
          () ->
              chequeFacade.finalizeCheque(retriever.getUniqueId(), heldItemStack)
                  .thenApply(MutableMessage::compile)
                  .thenAccept(retriever::sendMessage)
                  .exceptionally(exception -> delegateCaughtException(logger, exception))
      );
    }
  }
}
