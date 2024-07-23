package pl.auroramc.bounties.visit;

import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.scheduler.Scheduler;

class VisitService implements VisitFacade {

  private final Scheduler scheduler;
  private final VisitRepository visitRepository;

  VisitService(final Scheduler scheduler, final VisitRepository visitRepository) {
    this.scheduler = scheduler;
    this.visitRepository = visitRepository;
  }

  @Override
  public CompletableFuture<Void> createVisit(final Visit visit) {
    return scheduler.run(ASYNC, () -> visitRepository.createVisit(visit));
  }

  @Override
  public CompletableFuture<Set<Visit>> getVisitsByUserId(final Long userId) {
    return scheduler.supply(ASYNC, () -> visitRepository.findVisitsByUserId(userId));
  }

  @Override
  public CompletableFuture<Set<Visit>> getVisitsByUserIdInTimeframe(
      final Long userId, final Instant from, final Instant to) {
    return scheduler.supply(
        ASYNC, () -> visitRepository.findVisitsByUserIdInTimeframe(userId, from, to));
  }
}
