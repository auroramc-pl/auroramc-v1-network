package pl.auroramc.commons.period;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class PeriodFormatter {

  private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
  private static final DateTimeFormatter LONG_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
  private static final DateTimeFormatter SHORT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private PeriodFormatter() {

  }

  public static String getFormattedPeriod(final Instant period) {
    return LONG_DATE_TIME_FORMATTER.format(ZonedDateTime.ofInstant(period, SYSTEM_ZONE_ID));
  }

  public static String getFormattedPeriodShortly(final Instant period) {
    return SHORT_DATE_TIME_FORMATTER.format(ZonedDateTime.ofInstant(period, SYSTEM_ZONE_ID));
  }
}
