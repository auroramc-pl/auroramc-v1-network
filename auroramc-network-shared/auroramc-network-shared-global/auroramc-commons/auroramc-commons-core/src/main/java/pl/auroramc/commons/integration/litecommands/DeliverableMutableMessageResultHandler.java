package pl.auroramc.commons.integration.litecommands;

import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import net.kyori.adventure.audience.Audience;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;

public class DeliverableMutableMessageResultHandler<T extends Audience>
    implements ResultHandler<T, DeliverableMutableMessage> {

  @Override
  public void handle(
      final Invocation<T> invocation,
      final DeliverableMutableMessage message,
      final ResultHandlerChain<T> chain
  ) {
    message.deliver(invocation.sender());
  }
}
