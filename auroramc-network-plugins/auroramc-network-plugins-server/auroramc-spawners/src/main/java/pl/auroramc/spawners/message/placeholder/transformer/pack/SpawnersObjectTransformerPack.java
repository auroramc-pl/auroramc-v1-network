package pl.auroramc.spawners.message.placeholder.transformer.pack;

import pl.auroramc.messages.placeholder.transformer.pack.ObjectTransformerPack;
import pl.auroramc.messages.placeholder.transformer.registry.ObjectTransformerRegistry;

public class SpawnersObjectTransformerPack implements ObjectTransformerPack {

  @Override
  public void register(final ObjectTransformerRegistry transformerRegistry) {
    transformerRegistry.register(new StringToEntityTypeTransformer());
  }
}
