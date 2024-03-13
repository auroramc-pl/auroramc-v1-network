package pl.auroramc.cheque.message;

import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.Handler;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import pl.auroramc.commons.message.MutableMessage;

public class MessageResultHandler implements Handler<CommandSender, MutableMessage> {

  @Override
  public void handle(
      final CommandSender sender,
      final LiteInvocation invocation,
      final MutableMessage message
  ) {
    Optional.ofNullable(message).map(MutableMessage::into).ifPresent(sender::sendMessage);
  }
}
