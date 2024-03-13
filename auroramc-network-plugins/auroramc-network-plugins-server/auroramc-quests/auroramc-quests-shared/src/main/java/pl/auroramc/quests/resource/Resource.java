package pl.auroramc.quests.resource;

import java.util.List;
import pl.auroramc.quests.resource.key.ResourceKey;

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
