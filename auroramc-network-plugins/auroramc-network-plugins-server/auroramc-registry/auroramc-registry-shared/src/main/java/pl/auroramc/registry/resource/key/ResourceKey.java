package pl.auroramc.registry.resource.key;

public class ResourceKey {

  private final String name;
  private Long id;

  public ResourceKey(final Long id, final String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
}
