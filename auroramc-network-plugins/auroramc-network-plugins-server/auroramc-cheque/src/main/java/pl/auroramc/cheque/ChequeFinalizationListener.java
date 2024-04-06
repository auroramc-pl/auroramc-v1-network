package pl.auroramc.cheque;

import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;
import static pl.auroramc.cheque.message.MessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.messages.message.display.MessageDisplay.CHAT;

import io.papermc.paper.util.Tick;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.cheque.message.MessageSource;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

class ChequeFinalizationListener implements Listener {

  private final Scheduler scheduler;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final ChequeFacade chequeFacade;

  ChequeFinalizationListener(final Scheduler scheduler, final MessageSource messageSource, final BukkitMessageCompiler messageCompiler, final ChequeFacade chequeFacade) {
    this.scheduler = scheduler;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
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

    if (chequeFacade.isCheque(heldItemStack.clone())) {
      // Cancel the event, as we want to run our check finalization in
      // the next tick, so it won't be instantly removed from the player's hand.
      event.setCancelled(true);

      scheduler.runLater(SYNC, Tick.of(1), () -> finalizeChequeDelegator(event.getPlayer(), heldItemStack));
    }
  }

  private void finalizeChequeDelegator(final Player retriever, final ItemStack heldItemStack) {
    chequeFacade
        .finalizeCheque(retriever.getUniqueId(), heldItemStack)
        .thenApply(chequeContext -> messageSource.chequeFinalized.placeholder(CONTEXT_PATH, chequeContext))
        .thenApply(messageCompiler::compile)
        .thenAccept(message -> message.render(retriever, CHAT))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
