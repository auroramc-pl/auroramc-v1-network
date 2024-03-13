package pl.auroramc.lobby.message;

import dev.rollczi.litecommands.handler.result.ResultHandler;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import pl.auroramc.commons.message.MutableMessage;

public class MessageResultHandler<T> implements ResultHandler<T, MutableMessage> {

  @Override
  public void handle(
      final Invocation<T> invocation,
      final MutableMessage result,
      final ResultHandlerChain<T> chain
  ) {
    chain.resolve(invocation, result.into());
  }
}
