package pl.auroramc.dailyrewards.visit;

import java.time.Duration;
import java.time.Instant;

public record VisitContext(Duration period, Instant startTime, Instant ditchTime) {

  public static VisitContext completed(final Visit visit) {
    return new VisitContext(
        visit.getSessionDuration(), visit.getSessionStartTime(), visit.getSessionDitchTime());
  }

  public static VisitContext uncompleted(final Duration period) {
    return new VisitContext(period, null, null);
  }
}
