package pl.auroramc.registry.resource.provider;

public final class ResourceProviderBuilder {

  private String name;

  private ResourceProviderBuilder() {}

  public static ResourceProviderBuilder newBuilder() {
    return new ResourceProviderBuilder();
  }

  public ResourceProviderBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public ResourceProvider build() {
    return new ResourceProvider(null, name);
  }
}
