package pl.auroramc.registry.observer;

public class Observer {

  private Long id;
  private Long userId;
  private Long providerId;
  private boolean enabled;

  public Observer(final Long id, final Long userId, final Long providerId, final boolean enabled) {
    this.id = id;
    this.userId = userId;
    this.providerId = providerId;
    this.enabled = enabled;
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

  public Long getProviderId() {
    return providerId;
  }

  public void setProviderId(final Long providerId) {
    this.providerId = providerId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }
}
