package pl.auroramc.auth.user;

import java.util.UUID;

public class User {

  private Long id;
  private final UUID uniqueId;
  private String username;
  private String password;
  private String email;
  private UUID premiumUniqueId;
  private boolean authenticated;

  public User(
      final Long id,
      final UUID uniqueId,
      final String username,
      final String password,
      final String email,
      final UUID premiumUniqueId,
      final boolean authenticated
  ) {
    this.id = id;
    this.uniqueId = uniqueId;
    this.username = username;
    this.password = password;
    this.email = email;
    this.premiumUniqueId = premiumUniqueId;
    this.authenticated = authenticated;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public UUID getPremiumUniqueId() {
    return premiumUniqueId;
  }

  public void setPremiumUniqueId(final UUID premiumUniqueId) {
    this.premiumUniqueId = premiumUniqueId;
  }

  public boolean isPremium() {
    return premiumUniqueId != null;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  public void setAuthenticated(final boolean authenticated) {
    this.authenticated = authenticated;
  }

  public boolean isRegistered() {
    return password != null;
  }
}
