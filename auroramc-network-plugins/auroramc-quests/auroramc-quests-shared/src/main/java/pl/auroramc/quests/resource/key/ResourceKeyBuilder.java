package pl.auroramc.quests.resource.key;

public final class ResourceKeyBuilder {

  private String name;

  public ResourceKeyBuilder name(final String name) {
    this.name = name;
    return this;
  }

  public ResourceKey build() {
    return new ResourceKey(null, name);
  }
}
