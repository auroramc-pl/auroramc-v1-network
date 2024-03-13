package pl.auroramc.commons.duration;

import static pl.auroramc.commons.duration.settings.DurationFormatterSettingsUtils.getDefaultFormatterSettings;
import static pl.auroramc.commons.duration.settings.DurationFormatterSettingsUtils.getFormatterSettingsShortly;

import pl.auroramc.commons.duration.settings.DurationFormatterSettings;

public enum DurationFormatterStyle {

  DEFAULT(getDefaultFormatterSettings()),
  SHORTLY(getFormatterSettingsShortly());

  final DurationFormatterSettings formatterSettings;

  DurationFormatterStyle(final DurationFormatterSettings formatterSettings) {
    this.formatterSettings = formatterSettings;
  }

  public DurationFormatterSettings getFormatterSettings() {
    return formatterSettings;
  }
}