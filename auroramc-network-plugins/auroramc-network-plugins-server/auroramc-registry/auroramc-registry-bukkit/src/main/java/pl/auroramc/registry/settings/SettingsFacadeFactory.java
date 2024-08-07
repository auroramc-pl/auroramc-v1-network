package pl.auroramc.registry.settings;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class SettingsFacadeFactory {

  private SettingsFacadeFactory() {}

  public static SettingsFacade getSettingsFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlSettingsRepository sqlSettingsRepository = new SqlSettingsRepository(juliet);
    sqlSettingsRepository.createLanguageAndSettingsSchemaIfRequired();
    return new SettingsService(scheduler, sqlSettingsRepository);
  }
}
