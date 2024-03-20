package pl.auroramc.commons.duration.settings;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static pl.auroramc.commons.plural.PluralizationCase.PLURAL_GENITIVE;
import static pl.auroramc.commons.plural.PluralizationCase.PLURAL_NOMINATIVE;
import static pl.auroramc.commons.plural.PluralizationCase.SINGULAR;

import pl.auroramc.commons.plural.variety.VarietiesByCases;

public final class DurationFormatterSettingsUtils {

  private DurationFormatterSettingsUtils() {}

  public static DurationFormatterSettings getDefaultFormatterSettings() {
    return DurationFormatterSettings.newBuilder()
        .withAggregatingPhrase(", ")
        .withAggregatingPhraseEnclosing(" oraz ")
        .withUnitForm(
            YEARS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "rok")
                .withPluralForm(PLURAL_NOMINATIVE, "lata")
                .withPluralForm(PLURAL_GENITIVE, "lat")
                .build())
        .withUnitForm(
            MONTHS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "miesiąc")
                .withPluralForm(PLURAL_NOMINATIVE, "miesiące")
                .withPluralForm(PLURAL_GENITIVE, "miesięcy")
                .build())
        .withUnitForm(
            WEEKS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "tydzień")
                .withPluralForm(PLURAL_NOMINATIVE, "tygodnie")
                .withPluralForm(PLURAL_GENITIVE, "tygodni")
                .build())
        .withUnitForm(
            DAYS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "dzień")
                .withPluralForm(PLURAL_NOMINATIVE, "dni")
                .withPluralForm(PLURAL_GENITIVE, "dni")
                .build())
        .withUnitForm(
            HOURS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "godzina")
                .withPluralForm(PLURAL_NOMINATIVE, "godziny")
                .withPluralForm(PLURAL_GENITIVE, "godzin")
                .build())
        .withUnitForm(
            MINUTES,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "minuta")
                .withPluralForm(PLURAL_NOMINATIVE, "minuty")
                .withPluralForm(PLURAL_GENITIVE, "minut")
                .build())
        .withUnitForm(
            SECONDS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "sekunda")
                .withPluralForm(PLURAL_NOMINATIVE, "sekundy")
                .withPluralForm(PLURAL_GENITIVE, "sekund")
                .build())
        .withUnitForm(
            MILLIS,
            VarietiesByCases.newBuilder()
                .withPluralForm(SINGULAR, "milisekunda")
                .withPluralForm(PLURAL_NOMINATIVE, "milisekundy")
                .withPluralForm(PLURAL_GENITIVE, "milisekund")
                .build())
        .build();
  }

  public static DurationFormatterSettings getFormatterSettingsShortly() {
    return DurationFormatterSettings.newBuilder()
        .withAggregatingPhrase(", ")
        .withAggregatingPhraseEnclosing(" i ")
        .withUnitForm(YEARS, VarietiesByCases.newBuilder().withPluralForm("r").build())
        .withUnitForm(MONTHS, VarietiesByCases.newBuilder().withPluralForm("m").build())
        .withUnitForm(WEEKS, VarietiesByCases.newBuilder().withPluralForm("t").build())
        .withUnitForm(DAYS, VarietiesByCases.newBuilder().withPluralForm("d").build())
        .withUnitForm(HOURS, VarietiesByCases.newBuilder().withPluralForm("h").build())
        .withUnitForm(MINUTES, VarietiesByCases.newBuilder().withPluralForm("min").build())
        .withUnitForm(SECONDS, VarietiesByCases.newBuilder().withPluralForm("s").build())
        .withUnitForm(MILLIS, VarietiesByCases.newBuilder().withPluralForm("ms").build())
        .build();
  }
}
