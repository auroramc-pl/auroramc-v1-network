package pl.auroramc.commons.integration.litecommands.v2;

import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.Handler;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageResultHandler implements Handler<CommandSender, MutableMessage> {

  @Override
  public void handle(
      final CommandSender sender,
      final LiteInvocation invocation,
      final MutableMessage message
  ) {
    Optional.ofNullable(message)
        .map(MutableMessage::compile)
        .ifPresent(sender::sendMessage);
  }
}
