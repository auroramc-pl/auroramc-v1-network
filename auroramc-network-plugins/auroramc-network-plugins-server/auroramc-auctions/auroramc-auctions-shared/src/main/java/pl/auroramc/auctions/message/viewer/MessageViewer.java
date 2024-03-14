package pl.auroramc.auctions.message.viewer;

public class MessageViewer {

  private Long userId;
  private boolean whetherReceiveMessages;

  public MessageViewer(final Long userId, final boolean whetherReceiveMessages) {
    this.userId = userId;
    this.whetherReceiveMessages = whetherReceiveMessages;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public boolean isWhetherReceiveMessages() {
    return whetherReceiveMessages;
  }

  public void setWhetherReceiveMessages(final boolean whetherReceiveMessages) {
    this.whetherReceiveMessages = whetherReceiveMessages;
  }
}
