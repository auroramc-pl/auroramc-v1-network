package pl.auroramc.gamble.stake.view;

import static pl.auroramc.commons.BukkitUtils.postToMainThread;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.gamble.gamble.GambleFactory.getGamble;

import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import pl.auroramc.commons.page.navigation.PageNavigator;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.coinflip.CoinSide;
import pl.auroramc.gamble.gamble.GambleContext;
import pl.auroramc.gamble.gamble.GambleFacade;
import pl.auroramc.gamble.gamble.Participant;
import pl.auroramc.gamble.message.MutableMessageSource;
import pl.auroramc.gamble.stake.StakeContext;
import pl.auroramc.gamble.stake.StakeFacade;

public class StakeViewListener implements Listener {

  private final Plugin plugin;
  private final Logger logger;
  private final Currency fundsCurrency;
  private final MutableMessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final GambleFacade gambleFacade;
  private final StakeFacade stakeFacade;
  private final StakeViewFacade stakeViewFacade;

  public StakeViewListener(
      final Plugin plugin,
      final Logger logger,
      final Currency fundsCurrency,
      final MutableMessageSource messageSource,
      final EconomyFacade economyFacade,
      final GambleFacade gambleFacade,
      final StakeFacade stakeFacade,
      final StakeViewFacade stakeViewFacade
  ) {
    this.plugin = plugin;
    this.logger = logger;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
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
          .map(direction ->
              direction.navigate(
                  stakeViewFacade.getPageCount(),
                  stakeView.getPageIndex()
              )
          )
          .flatMap(stakeViewFacade::getStakeView)
          .ifPresent(event.getWhoClicked()::openInventory);
    }
  }

  public void requestClickCancelling(final InventoryClickEvent event) {
    event.setCancelled(true);
  }

  public void requestStakeFinalizing(final InventoryClickEvent event, final StakeContext stakeContext) {
    final Player player = (Player) event.getWhoClicked();
    if (stakeContext.initiator().uniqueId().equals(player.getUniqueId())) {
      player.sendMessage(
          messageSource.stakeFinalizationSelf
              .compile()
      );
      return;
    }

    economyFacade.has(player.getUniqueId(), fundsCurrency, stakeContext.stake())
        .thenAccept(whetherPlayerHasEnoughFunds ->
            completeStakeFinalization(event, stakeContext, whetherPlayerHasEnoughFunds)
        )
        .thenAccept(state -> postToMainThread(plugin, player::closeInventory))
        .thenAccept(state -> postToMainThread(plugin, stakeViewFacade::recalculate))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void completeStakeFinalization(
      final InventoryClickEvent event, final StakeContext stakeContext, final boolean whetherPlayerHasEnoughFunds) {
    final Player player = (Player) event.getWhoClicked();
    if (!whetherPlayerHasEnoughFunds) {
      player.sendMessage(
          messageSource.stakeFinalizationMissingBalance
              .compile()
      );
      return;
    }

    stakeFacade.deleteStakeContext(stakeContext);

    gambleFacade.settleGamble(
        getGamble(
            GambleContext.newBuilder()
                .gambleUniqueId(UUID.randomUUID())
                .stake(stakeContext.stake())
                .initiator(stakeContext.initiator())
                .competitor(
                    Participant.newBuilder()
                        .uniqueId(event.getWhoClicked().getUniqueId())
                        .username(event.getWhoClicked().getName())
                        .prediction(((CoinSide) stakeContext.initiator().prediction()).opposite())
                        .build()
                )
                .build(),
            stakeContext
        )
    );
  }
}
