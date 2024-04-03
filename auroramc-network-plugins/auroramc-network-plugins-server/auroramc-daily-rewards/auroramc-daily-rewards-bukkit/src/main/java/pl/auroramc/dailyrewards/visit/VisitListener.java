package pl.auroramc.dailyrewards.visit;

import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.dailyrewards.DailyRewardsConfig;
import pl.auroramc.registry.user.UserFacade;

public class VisitListener implements Listener {

  private final Logger logger;
  private final UserFacade userFacade;
  private final VisitFacade visitFacade;
  private final VisitController visitController;
  private final DailyRewardsConfig dailyRewardsConfig;

  public VisitListener(
      final Logger logger,
      final UserFacade userFacade,
      final VisitFacade visitFacade,
      final VisitController visitController,
      final DailyRewardsConfig dailyRewardsConfig) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.visitFacade = visitFacade;
    this.visitController = visitController;
    this.dailyRewardsConfig = dailyRewardsConfig;
  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {
    userFacade
        .getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenAccept(user -> visitController.startVisitTracking(user.getUniqueId()))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();

    final Instant visitStartTime = visitController.getVisitStartTime(player.getUniqueId());
    final Instant visitDitchTime = now();

    final boolean isUntrackedSession = visitStartTime == null;
    if (isUntrackedSession) {
      logger.warning(
          "Found an untracked visit for %s (%s)".formatted(player.getName(), player.getUniqueId()));
      return;
    }

    final Duration visitPeriod = visitController.gatherVisitPeriod(player.getUniqueId());
    if (visitPeriod.compareTo(dailyRewardsConfig.visitBuffer) <= 0) {
      return;
    }

    userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(user -> new Visit(user.getId(), visitPeriod, visitStartTime, visitDitchTime))
        .thenCompose(visitFacade::createVisit)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
