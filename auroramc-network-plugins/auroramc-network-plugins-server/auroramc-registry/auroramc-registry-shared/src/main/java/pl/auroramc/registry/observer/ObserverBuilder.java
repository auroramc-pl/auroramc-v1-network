package pl.auroramc.registry.observer;

public class ObserverBuilder {

  private Long userId;
  private Long providerId;
  private boolean enabled;

  private ObserverBuilder() {}

  public static ObserverBuilder newBuilder() {
    return new ObserverBuilder();
  }

  public ObserverBuilder userId(Long userId) {
    this.userId = userId;
    return this;
  }

  public ObserverBuilder providerId(Long providerId) {
    this.providerId = providerId;
    return this;
  }

  public ObserverBuilder enabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public Observer build() {
    return new Observer(null, userId, providerId, enabled);
  }
}
