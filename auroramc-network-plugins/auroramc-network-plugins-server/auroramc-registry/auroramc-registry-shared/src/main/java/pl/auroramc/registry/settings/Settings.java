package pl.auroramc.registry.settings;

import java.util.Locale;

public class Settings {

  private Long id;
  private Long userId;
  private Locale locale;

  public Settings(final Long id, final Long userId, final Locale locale) {
    this.id = id;
    this.userId = userId;
    this.locale = locale;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(final Locale locale) {
    this.locale = locale;
  }
}
