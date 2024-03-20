package pl.auroramc.commons.period;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public final class PeriodUtils {

  private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
  private static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59, 999_999_999);

  private PeriodUtils() {}

  public static Instant getMaximumTimeOfDay(final Instant value) {
    return LocalDate.ofInstant(value, SYSTEM_ZONE_ID)
        .atTime(END_OF_DAY)
        .toInstant(SYSTEM_ZONE_ID.getRules().getOffset(value));
  }

  public static Instant getMinimumTimeOfDay(final Instant value) {
    return LocalDate.ofInstant(value, SYSTEM_ZONE_ID)
        .atStartOfDay()
        .toInstant(SYSTEM_ZONE_ID.getRules().getOffset(value));
  }
}
