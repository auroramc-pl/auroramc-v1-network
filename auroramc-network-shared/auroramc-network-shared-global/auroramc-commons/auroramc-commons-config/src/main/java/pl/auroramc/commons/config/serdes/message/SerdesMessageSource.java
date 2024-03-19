package pl.auroramc.commons.config.serdes.message;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import org.jetbrains.annotations.NotNull;

public class SerdesMessageSource implements OkaeriSerdesPack {

  @Override
  public void register(final @NotNull SerdesRegistry registry) {
    registry.register(new MutableMessageToStringTransformer());
    registry.register(new MutableMessageDecorationSerializer());
    registry.register(new DeliverableMutableMessageSerializer());
  }
}
