package pl.auroramc.dailyrewards.visit;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface VisitFacade {

  CompletableFuture<Void> createVisit(final Visit visit);

  CompletableFuture<Set<Visit>> getVisitsByUserId(final Long userId);

  CompletableFuture<Set<Visit>> getVisitsByUserIdBetween(final Long userId, final Instant from, final Instant to);
}
