package pl.auroramc.bounty.visit;

import java.time.Instant;
import java.util.Set;

interface VisitRepository {

  void createVisit(final Visit visit);

  Set<Visit> findVisitsByUserId(final Long userId);

  Set<Visit> findVisitsByUserIdInTimeframe(final Long userId, final Instant from, final Instant to);
}
