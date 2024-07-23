package pl.auroramc.bounties.bounty;

import static java.time.Duration.ZERO;
import static java.time.LocalDate.now;
import static java.time.LocalTime.MAX;
import static java.time.ZoneOffset.UTC;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import panda.std.Pair;
import pl.auroramc.bounties.progress.BountyProgress;
import pl.auroramc.bounties.progress.BountyProgressFacade;
import pl.auroramc.bounties.visit.Visit;
import pl.auroramc.bounties.visit.VisitController;
import pl.auroramc.bounties.visit.VisitFacade;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

@Permission("auroramc.bounties.bounty")
@Command(
    name = "bounty",
    aliases = {"daily", "nagroda", "nagrody"})
public class BountyCommand {

  private final Scheduler scheduler;
  private final UserFacade userFacade;
  private final VisitFacade visitFacade;
  private final VisitController visitController;
  private final BountyProgressFacade bountyProgressFacade;
  private final BountiesViewRenderer bountiesViewRenderer;

  public BountyCommand(
      final Scheduler scheduler,
      final UserFacade userFacade,
      final VisitFacade visitFacade,
      final VisitController visitController,
      final BountyProgressFacade bountyProgressFacade,
      final BountiesViewRenderer bountiesViewRenderer) {
    this.scheduler = scheduler;
    this.userFacade = userFacade;
    this.visitFacade = visitFacade;
    this.visitController = visitController;
    this.bountyProgressFacade = bountyProgressFacade;
    this.bountiesViewRenderer = bountiesViewRenderer;
  }

  @Execute
  public void bounty(final @Context Player player) {
    userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenApply(User::getId)
        .thenCompose(bountyProgressFacade::retrieveBountyProgress)
        .thenCompose(
            bountyProgress ->
                getAggregatedPlaytimeFromToday(player, bountyProgress)
                    .thenApply(aggregatedPlaytime -> Pair.of(bountyProgress, aggregatedPlaytime))
                    .exceptionally(CompletableFutureUtils::delegateCaughtException))
        .thenAccept(
            bountyProgressWithAggregatedPlaytime ->
                scheduler.run(
                    SYNC,
                    () ->
                        bountiesViewRenderer.render(
                            player,
                            bountyProgressWithAggregatedPlaytime.getFirst(),
                            bountyProgressWithAggregatedPlaytime.getSecond())))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Duration> getAggregatedPlaytimeFromToday(
      final Player player, final BountyProgress bountyProgress) {
    final LocalDate today = now();
    final Instant minimumTimeOfDay = today.atStartOfDay().toInstant(UTC);
    final Instant maximumTimeOfDay = today.atTime(MAX).toInstant(UTC);
    return visitFacade
        .getVisitsByUserIdInTimeframe(bountyProgress.getId(), minimumTimeOfDay, maximumTimeOfDay)
        .thenApply(this::getAggregatedPlaytime)
        .thenApply(aggregatedPlaytime -> appendCurrentSession(player, aggregatedPlaytime))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private Duration getAggregatedPlaytime(final Set<Visit> visits) {
    return visits.stream().map(Visit::getDuration).reduce(Duration::plus).orElse(ZERO);
  }

  private Duration appendCurrentSession(final Player player, final Duration aggregatedPlaytime) {
    return aggregatedPlaytime.plus(visitController.getVisitDuration(player.getUniqueId()));
  }
}
