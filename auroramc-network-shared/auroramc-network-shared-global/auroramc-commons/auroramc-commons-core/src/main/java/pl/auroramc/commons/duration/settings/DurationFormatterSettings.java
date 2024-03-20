package pl.auroramc.commons.duration.settings;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import pl.auroramc.commons.plural.variety.VarietiesByCases;

public class DurationFormatterSettings {

  private final String aggregatingPhrase;
  private final String aggregatingPhraseEnclosing;
  private final Map<ChronoUnit, VarietiesByCases> unitForms;

  DurationFormatterSettings(
      final String aggregatingPhrase,
      final String aggregatingPhraseEnclosing,
      final Map<ChronoUnit, VarietiesByCases> unitForms) {
    this.aggregatingPhrase = aggregatingPhrase;
    this.aggregatingPhraseEnclosing = aggregatingPhraseEnclosing;
    this.unitForms = unitForms;
  }

  public static DurationFormatterSettingsBuilder newBuilder() {
    return new DurationFormatterSettingsBuilder();
  }

  public String getAggregatingPhrase() {
    return aggregatingPhrase;
  }

  public String getAggregatingPhraseEnclosing() {
    return aggregatingPhraseEnclosing;
  }

  public VarietiesByCases getUnitForm(final ChronoUnit unit) {
    return unitForms.get(unit);
  }

  public DurationFormatterSettingsBuilder toBuilder() {
    return new DurationFormatterSettingsBuilder(unitForms);
  }
}
