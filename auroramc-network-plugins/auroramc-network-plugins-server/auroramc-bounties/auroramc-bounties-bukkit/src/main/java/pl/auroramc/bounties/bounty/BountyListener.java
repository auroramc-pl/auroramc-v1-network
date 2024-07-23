package pl.auroramc.bounties.bounty;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static pl.auroramc.bounties.bounty.BountyUtils.INITIAL_DAY;
import static pl.auroramc.bounties.bounty.BountyUtils.MAXIMUM_INACTIVITY;
import static pl.auroramc.commons.concurrent.CompletableFutureUtils.NIL;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.auroramc.bounties.progress.BountyProgress;
import pl.auroramc.bounties.progress.BountyProgressFacade;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class BountyListener implements Listener {

  private final UserFacade userFacade;
  private final BountyProgressFacade bountyProgressFacade;

  public BountyListener(
      final UserFacade userFacade, final BountyProgressFacade bountyProgressFacade) {
    this.userFacade = userFacade;
    this.bountyProgressFacade = bountyProgressFacade;
  }

  @EventHandler
  public void onBountyValidation(final PlayerJoinEvent event) {
    userFacade
        .getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenApply(User::getId)
        .thenCompose(bountyProgressFacade::retrieveBountyProgress)
        .thenCompose(this::validateBountyProgress)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Void> validateBountyProgress(final BountyProgress bountyProgress) {
    final LocalDate today = now();
    final LocalDate acquisitionDate = bountyProgress.getAcquisitionDate();

    final boolean requiresStrikeValidation = bountyProgress.getDay() > INITIAL_DAY;
    if (requiresStrikeValidation) {
      final long differenceInDays = DAYS.between(acquisitionDate, today);
      if (differenceInDays > MAXIMUM_INACTIVITY) {
        bountyProgress.setDay(INITIAL_DAY);
        return bountyProgressFacade
            .updateBountyProgress(bountyProgress)
            .exceptionally(CompletableFutureUtils::delegateCaughtException);
      }
    }

    return NIL;
  }
}
