package pl.auroramc.auctions.message;

import java.util.Collection;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;
import pl.auroramc.commons.message.MutableMessage;

class MessageService implements MessageFacade {

  private final MessageViewerFacade messageViewerFacade;

  MessageService(final MessageViewerFacade messageViewerFacade) {
    this.messageViewerFacade = messageViewerFacade;
  }

  @Override
  public void deliverMessage(final Player target, final MutableMessage message) {
    messageViewerFacade
        .getMessageViewerByUserUniqueId(target.getUniqueId())
        .thenAccept(messageViewer -> {
          if (messageViewer.isWhetherReceiveMessages()) {
            target.sendMessage(message.compile());
          }
        });
  }

  @Override
  public void deliverMessage(final Collection<? extends Player> targets, final MutableMessage message) {
    targets.forEach(target -> deliverMessage(target, message));
  }
}
