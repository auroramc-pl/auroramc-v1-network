package pl.auroramc.commons.duration;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import pl.auroramc.commons.duration.settings.DurationFormatterSettings;
import pl.auroramc.commons.plural.Pluralizer;
import pl.auroramc.commons.plural.variety.VarietiesByCases;

public final class DurationFormatter {

  private static final ChronoUnit[] UNITS =
      new ChronoUnit[] {YEARS, MONTHS, WEEKS, DAYS, HOURS, MINUTES, SECONDS, MILLIS};
  private static final long[] UNIT_PERIODS =
      new long[] {
        1_000L * 60 * 60 * 24 * 365,
        1_000L * 60 * 60 * 24 * 30,
        1_000L * 60 * 60 * 24 * 7,
        1_000L * 60 * 60 * 24,
        1_000L * 60 * 60,
        1_000L * 60,
        1_000L
      };
  private static final long SMALLEST_UNIT_PERIOD = UNIT_PERIODS[UNIT_PERIODS.length - 1];
  private final Pluralizer pluralizer;
  private final DurationFormatterSettings formatterSettings;

  public DurationFormatter(
      final Pluralizer pluralizer, final DurationFormatterSettings formatterSettings) {
    this.pluralizer = pluralizer;
    this.formatterSettings = formatterSettings;
  }

  public DurationFormatter(
      final Pluralizer pluralizer, final DurationFormatterStyle formattingStyle) {
    this(pluralizer, formattingStyle.getFormatterSettings());
  }

  public String getFormattedDuration(final Duration period) {
    final long millis = period.toMillis();
    if (SMALLEST_UNIT_PERIOD > millis) {
      final VarietiesByCases unitForm = formatterSettings.getUnitForm(MILLIS);
      return "%d %s".formatted(millis, pluralizer.pluralize(unitForm, millis));
    }

    final StringBuilder chain = new StringBuilder();

    String lastMatch = null;
    long remainingMillis = millis;
    for (int index = 0; index < UNIT_PERIODS.length; index++) {
      if (remainingMillis < SMALLEST_UNIT_PERIOD) {
        break;
      }

      final long divider = UNIT_PERIODS[index];
      if (divider > remainingMillis) {
        continue;
      }

      final long matches = remainingMillis / divider;
      if (matches > 0) {
        final VarietiesByCases unitForm = formatterSettings.getUnitForm(UNITS[index]);
        final String currentUnit =
            "%d %s".formatted(matches, pluralizer.pluralize(unitForm, matches));

        if (lastMatch != null) {
          chain.append(lastMatch).append(formatterSettings.getAggregatingPhrase());
        }
        lastMatch = currentUnit;

        remainingMillis -= matches * divider;
      }
    }

    if (lastMatch != null) {
      if (chain.isEmpty()) {
        chain.append(lastMatch);
        return chain.toString();
      }

      chain
          .delete(
              chain.length() - formatterSettings.getAggregatingPhrase().length(), chain.length())
          .append(formatterSettings.getAggregatingPhraseEnclosing())
          .append(lastMatch);
    }

    return chain.toString();
  }
}
