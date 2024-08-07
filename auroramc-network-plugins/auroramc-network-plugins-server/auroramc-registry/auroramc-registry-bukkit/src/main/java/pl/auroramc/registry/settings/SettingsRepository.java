package pl.auroramc.registry.settings;

interface SettingsRepository {

  Settings findSettingsByUserId(final Long userId);

  void createSettings(final Settings settings);

  void updateSettings(final Settings settings);
}
