package pl.auroramc.auctions.message;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;

class MessageService implements MessageFacade {

  private final MessageViewerFacade messageViewerFacade;

  MessageService(final MessageViewerFacade messageViewerFacade) {
    this.messageViewerFacade = messageViewerFacade;
  }

  @Override
  public void deliverMessage(final Player target, final Component message) {
    messageViewerFacade
        .getMessageViewerByUserUniqueId(target.getUniqueId())
        .thenAccept(messageViewer -> {
          if (messageViewer.isWhetherReceiveMessages()) {
            target.sendMessage(message);
          }
        });
  }

  @Override
  public void deliverMessage(final Collection<? extends Player> targets, final Component message) {
    targets.forEach(target -> deliverMessage(target, message));
  }
}
