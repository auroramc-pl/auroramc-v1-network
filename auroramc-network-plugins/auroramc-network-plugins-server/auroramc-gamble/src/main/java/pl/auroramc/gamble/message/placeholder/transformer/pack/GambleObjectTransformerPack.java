package pl.auroramc.gamble.message.placeholder.transformer.pack;

import pl.auroramc.messages.placeholder.transformer.pack.ObjectTransformerPack;
import pl.auroramc.messages.placeholder.transformer.registry.ObjectTransformerRegistry;

public class GambleObjectTransformerPack implements ObjectTransformerPack {

  @Override
  public void register(final ObjectTransformerRegistry transformerRegistry) {
    transformerRegistry.register(new StringToCoinSideTransformer());
    transformerRegistry.register(new StringToGambleKeyTransformer());
  }
}
