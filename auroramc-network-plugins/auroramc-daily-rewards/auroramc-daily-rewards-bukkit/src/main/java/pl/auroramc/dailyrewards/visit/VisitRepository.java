package pl.auroramc.dailyrewards.visit;

import java.time.Instant;
import java.util.Set;

interface VisitRepository {

  void createVisit(final Visit visit);

  Set<Visit> findVisitsByUserId(
      final Long userId
  );

  Set<Visit> findVisitsByUserIdBetween(
      final Long userId, final Instant from, final Instant to
  );
}
