package pl.auroramc.quests.resource.key;

public class ResourceKey {

  private Long id;
  private final String name;

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
