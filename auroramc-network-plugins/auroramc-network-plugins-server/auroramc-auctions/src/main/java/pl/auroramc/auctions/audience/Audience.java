package pl.auroramc.auctions.audience;

public class Audience {

  private Long userId;
  private boolean allowsMessages;

  public Audience(final Long userId, final boolean allowsMessages) {
    this.userId = userId;
    this.allowsMessages = allowsMessages;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public boolean isAllowsMessages() {
    return allowsMessages;
  }

  public void setAllowsMessages(final boolean allowsMessages) {
    this.allowsMessages = allowsMessages;
  }
}
