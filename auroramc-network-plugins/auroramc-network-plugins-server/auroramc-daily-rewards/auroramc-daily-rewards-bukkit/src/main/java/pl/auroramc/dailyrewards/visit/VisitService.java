package pl.auroramc.dailyrewards.visit;

import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.time.Instant;
import java.util.Set;
import java.util.logging.Logger;

class VisitService implements VisitFacade {

  private final Logger logger;
  private final VisitRepository visitRepository;

  VisitService(final Logger logger, final VisitRepository visitRepository) {
    this.logger = logger;
    this.visitRepository = visitRepository;
  }

  @Override
  public void createVisit(final Visit visit) {
    runAsync(() -> visitRepository.createVisit(visit))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public Set<Visit> getVisitsByUserId(final Long userId) {
    return visitRepository.findVisitsByUserId(userId);
  }

  @Override
  public Set<Visit> getVisitsByUserIdBetween(
      final Long userId, final Instant from, final Instant to) {
    return visitRepository.findVisitsByUserIdBetween(userId, from, to);
  }
}
