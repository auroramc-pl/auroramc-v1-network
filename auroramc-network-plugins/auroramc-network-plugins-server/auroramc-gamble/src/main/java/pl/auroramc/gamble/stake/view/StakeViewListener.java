package pl.auroramc.gamble.stake.view;

import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;
import static pl.auroramc.gamble.gamble.GambleFactory.getGamble;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.bukkit.page.navigation.PageNavigator;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.gamble.coinflip.CoinSide;
import pl.auroramc.gamble.gamble.context.GambleContext;
import pl.auroramc.gamble.gamble.GambleFacade;
import pl.auroramc.gamble.participant.Participant;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.context.StakeContext;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.Viewer;

public class StakeViewListener implements Listener {

  private final Scheduler scheduler;
  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final EconomyFacade economyFacade;
  private final GambleFacade gambleFacade;
  private final StakeFacade stakeFacade;
  private final StakeViewFacade stakeViewFacade;

  public StakeViewListener(
      final Scheduler scheduler,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EconomyFacade economyFacade,
      final GambleFacade gambleFacade,
      final StakeFacade stakeFacade,
      final StakeViewFacade stakeViewFacade) {
    this.scheduler = scheduler;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.economyFacade = economyFacade;
    this.gambleFacade = gambleFacade;
    this.stakeFacade = stakeFacade;
    this.stakeViewFacade = stakeViewFacade;
  }

  @EventHandler
  public void onStakeViewInteraction(final InventoryClickEvent event) {
    if (event.getInventory().getHolder() instanceof StakeView stakeView) {
      requestClickCancelling(event);

      stakeView
          .getStakeContextBySlot(event.getSlot())
          .ifPresent(stakeContext -> requestStakeFinalizing(event, stakeContext));

      stakeView
          .getNavigatorBySlot(event.getSlot())
          .map(PageNavigator::direction)
          .map(
              direction ->
                  direction.navigate(stakeViewFacade.getPageCount(), stakeView.getPageIndex()))
          .flatMap(stakeViewFacade::getStakeView)
          .ifPresent(event.getWhoClicked()::openInventory);
    }
  }

  public void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public void requestStakeFinalizing(
      final InventoryClickEvent event, final StakeContext stakeContext) {
    final Player player = (Player) event.getWhoClicked();
    final Viewer viewer = BukkitViewer.wrap(player);
    if (stakeContext.initiator().uniqueId().equals(player.getUniqueId())) {
      viewer.deliver(messageCompiler.compile(messageSource.stakeFinalizationSelf));
      return;
    }

    economyFacade
        .has(player.getUniqueId(), fundsCurrency, stakeContext.stake())
        .thenAccept(
            whetherPlayerHasEnoughFunds ->
                completeStakeFinalization(event, stakeContext, whetherPlayerHasEnoughFunds))
        .thenAccept(state -> scheduler.run(SYNC, player::closeInventory))
        .thenAccept(state -> scheduler.run(SYNC, stakeViewFacade::recalculate))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void completeStakeFinalization(
      final InventoryClickEvent event,
      final StakeContext stakeContext,
      final boolean whetherPlayerHasEnoughFunds) {
    final Player player = (Player) event.getWhoClicked();
    final Viewer viewer = BukkitViewer.wrap(player);
    if (!whetherPlayerHasEnoughFunds) {
      viewer.deliver(messageCompiler.compile(messageSource.stakeFinalizationMissingBalance));
      return;
    }

    stakeFacade.deleteStakeContext(stakeContext);

    // Until we have only two sides of a coin, we can assume that the opposite side of the
    // initiator's prediction is the competitor's prediction.
    final Object prediction = ((CoinSide) stakeContext.initiator().prediction()).opposite();
    gambleFacade.settleGamble(
        getGamble(
            getGambleContext((Player) event.getWhoClicked(), stakeContext, prediction),
            stakeContext));
  }

  private GambleContext getGambleContext(
      final Player player, final StakeContext stakeContext, final Object prediction) {
    return GambleContext.newBuilder()
        .gambleUniqueId(UUID.randomUUID())
        .stake(stakeContext.stake())
        .initiator(stakeContext.initiator())
        .competitor(
            Participant.newBuilder()
                .uniqueId(player.getUniqueId())
                .username(player.getName())
                .prediction(prediction)
                .build())
        .build();
  }
}
