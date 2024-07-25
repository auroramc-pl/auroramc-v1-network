package pl.auroramc.registry.resource.key;

public class ResourceKey {

  private Long id;
  private Long providerId;
  private String name;

  public ResourceKey(final Long id, final Long providerId, final String name) {
    this.id = id;
    this.providerId = providerId;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getProviderId() {
    return providerId;
  }

  public void setProviderId(final Long providerId) {
    this.providerId = providerId;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
