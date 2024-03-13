package pl.auroramc.punishments.user;

import java.util.UUID;

public class User {

  private Long id;
  private UUID uniqueId;
  private String username;

  public User(final Long id, final UUID uniqueId, final String username) {
    this.id = id;
    this.uniqueId = uniqueId;
    this.username = username;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(final UUID uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }
}
