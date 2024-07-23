package pl.auroramc.bounties.bounty;

import static java.time.LocalDate.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static pl.auroramc.bounties.bounty.BountyMessageSourcePaths.DAY_PATH;
import static pl.auroramc.bounties.bounty.BountyUtils.MAXIMUM_INACTIVITY;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;

import java.time.Duration;
import java.time.LocalDate;
import org.bukkit.entity.Player;
import pl.auroramc.bounties.BountyConfig;
import pl.auroramc.bounties.progress.BountyProgress;
import pl.auroramc.bounties.progress.BountyProgressFacade;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.integrations.reward.Reward;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public class BountyController {

  private final Scheduler scheduler;
  private final BountyConfig bountyConfig;
  private final BountyProgressFacade bountyProgressFacade;
  private final BountyMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  public BountyController(
      final Scheduler scheduler,
      final BountyConfig bountyConfig,
      final BountyProgressFacade bountyProgressFacade,
      final BountyMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.scheduler = scheduler;
    this.bountyConfig = bountyConfig;
    this.bountyProgressFacade = bountyProgressFacade;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  public void acquireBounty(
      final Player player,
      final Bounty bounty,
      final BountyProgress bountyProgress,
      final Duration aggregatedPlaytime) {
    final LocalDate today = now();
    final LocalDate acquisitionDate = bountyProgress.getAcquisitionDate();
    if (bounty.getDay() == bountyProgress.getDay()
        && DAYS.between(acquisitionDate, today) >= MAXIMUM_INACTIVITY
        && aggregatedPlaytime.compareTo(bountyConfig.bountyBuffer) >= 0) {
      bountyProgress.setDay(bounty.getDay() + 1);
      bountyProgress.setAcquisitionDate(today);
      bountyProgressFacade
          .updateBountyProgress(bountyProgress)
          .exceptionally(CompletableFutureUtils::delegateCaughtException);
      scheduler.run(SYNC, () -> processBounty(player, bounty));
    }
  }

  private void processBounty(final Player player, final Bounty bounty) {
    player.closeInventory();
    for (final Reward reward : bounty.getRewards()) {
      reward.assign(player);
    }
    messageCompiler
        .compile(messageSource.bountyAcquired.placeholder(DAY_PATH, bounty.getDay() + 1))
        .deliver(player);
  }
}
