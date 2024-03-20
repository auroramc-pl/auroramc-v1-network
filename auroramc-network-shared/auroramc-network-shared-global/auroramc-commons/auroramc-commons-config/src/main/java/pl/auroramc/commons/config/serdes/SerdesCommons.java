package pl.auroramc.commons.config.serdes;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import org.jetbrains.annotations.NotNull;

public class SerdesCommons implements OkaeriSerdesPack {

  @Override
  public void register(final @NotNull SerdesRegistry registry) {
    registry.register(new eu.okaeri.configs.serdes.commons.SerdesCommons());
    registry.register(new StringToDecimalFormatTransformer());
    registry.register(new StringToLocaleTransformer());
    registry.register(new StringToZoneIdTransformer());
  }
}
