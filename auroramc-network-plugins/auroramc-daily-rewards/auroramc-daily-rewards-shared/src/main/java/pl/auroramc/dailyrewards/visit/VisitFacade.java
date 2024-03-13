package pl.auroramc.dailyrewards.visit;

import java.time.Instant;
import java.util.Set;

public interface VisitFacade {

  void createVisit(final Visit visit);

  Set<Visit> getVisitsByUserId(
      final Long userId
  );

  Set<Visit> getVisitsByUserIdBetween(
      final Long userId, final Instant from, final Instant to
  );
}
