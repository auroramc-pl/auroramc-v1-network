package pl.auroramc.commons.config.serdes.message;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.commons.message.MutableMessage;

class MutableMessageToStringTransformer extends BidirectionalTransformer<String, MutableMessage> {

  MutableMessageToStringTransformer() {

  }

  @Override
  public GenericsPair<String, MutableMessage> getPair() {
    return genericsPair(String.class, MutableMessage.class);
  }

  @Override
  public MutableMessage leftToRight(
      final @NotNull String data,
      final @NotNull SerdesContext serdesContext
  ) {
    return MutableMessage.of(data);
  }

  @Override
  public String rightToLeft(
      final @NotNull MutableMessage data,
      final @NotNull SerdesContext serdesContext
  ) {
    return data.getTemplate();
  }
}
