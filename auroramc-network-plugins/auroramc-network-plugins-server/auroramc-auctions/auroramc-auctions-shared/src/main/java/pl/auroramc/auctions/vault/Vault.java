package pl.auroramc.auctions.vault;

public class Vault {

  private Long id;
  private Long userId;

  public Vault(final Long id, final Long userId) {
    this.id = id;
    this.userId = userId;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }
}
