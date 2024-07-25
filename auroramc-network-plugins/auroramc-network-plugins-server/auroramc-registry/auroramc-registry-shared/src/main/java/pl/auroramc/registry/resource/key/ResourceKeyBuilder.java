package pl.auroramc.registry.resource.key;

public final class ResourceKeyBuilder {

  private Long providerId;
  private String name;

  private ResourceKeyBuilder() {}

  public static ResourceKeyBuilder newBuilder() {
    return new ResourceKeyBuilder();
  }

  public ResourceKeyBuilder withProviderId(final Long providerId) {
    this.providerId = providerId;
    return this;
  }

  public ResourceKeyBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public ResourceKey build() {
    return new ResourceKey(null, providerId, name);
  }
}
