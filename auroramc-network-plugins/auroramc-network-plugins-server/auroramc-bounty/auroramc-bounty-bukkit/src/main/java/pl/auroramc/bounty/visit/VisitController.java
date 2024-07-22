package pl.auroramc.bounty.visit;

import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VisitController {

  private final Map<UUID, Instant> visitStartTimeByUniqueId;

  public VisitController() {
    this.visitStartTimeByUniqueId = new ConcurrentHashMap<>();
  }

  public void startVisitTracking(final UUID uniqueId) {
    visitStartTimeByUniqueId.put(uniqueId, now());
  }

  public Duration getVisitDuration(final UUID uniqueId) {
    return visitStartTimeByUniqueId.containsKey(uniqueId)
        ? between(visitStartTimeByUniqueId.get(uniqueId), now())
        : ZERO;
  }

  public Instant getVisitStartTime(final UUID uniqueId) {
    return visitStartTimeByUniqueId.get(uniqueId);
  }

  public Duration gatherVisitDuration(final UUID uniqueId) {
    if (visitStartTimeByUniqueId.containsKey(uniqueId)) {
      final Instant visitStartTime = visitStartTimeByUniqueId.get(uniqueId);
      final Instant visitDitchTime = now();
      visitStartTimeByUniqueId.remove(uniqueId);
      return between(visitStartTime, visitDitchTime);
    }

    return ZERO;
  }
}
