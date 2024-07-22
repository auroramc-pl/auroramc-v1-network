package pl.auroramc.bounty.visit;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface VisitFacade {

  CompletableFuture<Void> createVisit(final Visit visit);

  CompletableFuture<Set<Visit>> getVisitsByUserId(final Long userId);

  CompletableFuture<Set<Visit>> getVisitsByUserIdInTimeframe(
      final Long userId, final Instant from, final Instant to);
}
