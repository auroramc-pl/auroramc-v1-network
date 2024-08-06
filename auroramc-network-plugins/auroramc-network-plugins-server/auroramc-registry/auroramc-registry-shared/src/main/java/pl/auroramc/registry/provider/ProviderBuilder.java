package pl.auroramc.registry.provider;

public final class ProviderBuilder {

  private String name;

  private ProviderBuilder() {}

  public static ProviderBuilder newBuilder() {
    return new ProviderBuilder();
  }

  public ProviderBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public Provider build() {
    return new Provider(null, name);
  }
}
