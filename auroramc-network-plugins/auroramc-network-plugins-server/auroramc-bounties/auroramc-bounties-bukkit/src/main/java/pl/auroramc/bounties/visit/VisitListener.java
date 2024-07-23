package pl.auroramc.bounties.visit;

import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.bounties.BountyConfig;
import pl.auroramc.registry.user.UserFacade;

public class VisitListener implements Listener {

  private final Logger logger;
  private final UserFacade userFacade;
  private final VisitFacade visitFacade;
  private final VisitController visitController;
  private final BountyConfig bountyConfig;

  public VisitListener(
      final Logger logger,
      final UserFacade userFacade,
      final VisitFacade visitFacade,
      final VisitController visitController,
      final BountyConfig bountyConfig) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.visitFacade = visitFacade;
    this.visitController = visitController;
    this.bountyConfig = bountyConfig;
  }

  @EventHandler
  public void onVisitStart(final PlayerJoinEvent event) {
    userFacade
        .getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenAccept(user -> visitController.startVisitTracking(user.getUniqueId()))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @EventHandler
  public void onVisitDitch(final PlayerQuitEvent event) {
    final Player player = event.getPlayer();

    final Instant startTime = visitController.getVisitStartTime(player.getUniqueId());
    final Instant ditchTime = now();

    final boolean isUntrackedSession = startTime == null;
    if (isUntrackedSession) {
      logger.warning(
          "Found an untracked visit for %s (%s)".formatted(player.getName(), player.getUniqueId()));
      return;
    }

    final Duration duration = visitController.gatherVisitDuration(player.getUniqueId());
    if (duration.compareTo(bountyConfig.visitBuffer) <= 0) {
      logger.fine(
          "Skipping visit for %s (%s) due to insufficient duration"
              .formatted(player.getName(), player.getUniqueId()));
      return;
    }

    userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(user -> new Visit(user.getId(), duration, startTime, ditchTime))
        .thenCompose(visitFacade::createVisit);
  }
}
