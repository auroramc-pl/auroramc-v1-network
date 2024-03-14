package pl.auroramc.commons.integration.litecommands.v2;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.Handler;
import java.util.Optional;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageResultHandler implements Handler<CommandSource, MutableMessage> {

  @Override
  public void handle(
      final CommandSource sender,
      final LiteInvocation invocation,
      final MutableMessage message
  ) {
    Optional.ofNullable(message)
        .map(MutableMessage::compile)
        .ifPresent(sender::sendMessage);
  }
}

