package pl.auroramc.registry.provider;

public class Provider {

  private Long id;
  private String name;

  public Provider(final Long id, final String name) {
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

  public void setName(final String name) {
    this.name = name;
  }
}
