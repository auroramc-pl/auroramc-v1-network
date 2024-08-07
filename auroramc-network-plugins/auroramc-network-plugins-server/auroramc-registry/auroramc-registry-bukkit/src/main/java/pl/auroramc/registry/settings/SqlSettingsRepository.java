package pl.auroramc.registry.settings;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Locale.forLanguageTag;
import static pl.auroramc.registry.settings.SqlSettingsRepositoryQuery.CREATE_LANGUAGES_SCHEMA;
import static pl.auroramc.registry.settings.SqlSettingsRepositoryQuery.CREATE_SETTINGS;
import static pl.auroramc.registry.settings.SqlSettingsRepositoryQuery.CREATE_SETTINGS_SCHEMA;
import static pl.auroramc.registry.settings.SqlSettingsRepositoryQuery.FIND_SETTINGS_BY_USER_ID;
import static pl.auroramc.registry.settings.SqlSettingsRepositoryQuery.UPDATE_SETTINGS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import moe.rafal.juliet.Juliet;

class SqlSettingsRepository implements SettingsRepository {

  private final Juliet juliet;

  public SqlSettingsRepository(final Juliet juliet) {
    this.juliet = juliet;
  }

  void createLanguageAndSettingsSchemaIfRequired() {
    try (final Connection connection = juliet.borrowConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(CREATE_LANGUAGES_SCHEMA);
      statement.execute(CREATE_SETTINGS_SCHEMA);
    } catch (final SQLException exception) {
      throw new SettingsRepositoryException(
          "Could not create settings schema, because of unexpected exception.", exception);
    }
  }

  @Override
  public Settings findSettingsByUserId(final Long userId) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(FIND_SETTINGS_BY_USER_ID)) {
      statement.setLong(1, userId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return mapResultSetToSettings(resultSet);
        }
      }
    } catch (final SQLException exception) {
      throw new SettingsRepositoryException(
          "Could not find settings by user id, because of unexpected exception.", exception);
    }
    return null;
  }

  @Override
  public void createSettings(final Settings settings) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement =
            connection.prepareStatement(CREATE_SETTINGS, RETURN_GENERATED_KEYS)) {
      statement.setLong(1, settings.getUserId());
      statement.setString(2, settings.getLocale().toLanguageTag());
      statement.executeUpdate();
      try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          settings.setId(generatedKeys.getLong(1));
        }
      }
    } catch (final SQLException exception) {
      throw new SettingsRepositoryException(
          "Could not create settings, because of unexpected exception.", exception);
    }
  }

  @Override
  public void updateSettings(final Settings settings) {
    try (final Connection connection = juliet.borrowConnection();
        final PreparedStatement statement = connection.prepareStatement(UPDATE_SETTINGS)) {
      statement.setString(1, settings.getLocale().toLanguageTag());
      statement.setLong(2, settings.getId());
      statement.executeUpdate();
    } catch (final SQLException exception) {
      throw new SettingsRepositoryException(
          "Could not update settings, because of unexpected exception.", exception);
    }
  }

  private Settings mapResultSetToSettings(final ResultSet resultSet) throws SQLException {
    return new Settings(
        resultSet.getLong(1), resultSet.getLong(2), forLanguageTag(resultSet.getString(3)));
  }
}
