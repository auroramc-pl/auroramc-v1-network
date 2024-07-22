package pl.auroramc.registry.resource;

import java.util.List;
import pl.auroramc.registry.resource.key.ResourceKey;

public class Resource {

  private final ResourceKey key;

  protected Resource(final ResourceKey key) {
    this.key = key;
  }

  public List<? extends Resource> children() {
    return List.of();
  }

  public ResourceKey getKey() {
    return key;
  }
}
