package pl.auroramc.commons.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.time.ZoneId;
import org.jetbrains.annotations.NotNull;

class StringToZoneIdTransformer extends BidirectionalTransformer<String, ZoneId> {

  @Override
  public GenericsPair<String, ZoneId> getPair() {
    return genericsPair(String.class, ZoneId.class);
  }

  @Override
  public ZoneId leftToRight(
      final @NotNull String data, final @NotNull SerdesContext serdesContext) {
    return ZoneId.of(data);
  }

  @Override
  public String rightToLeft(
      final @NotNull ZoneId data, final @NotNull SerdesContext serdesContext) {
    return data.getId();
  }
}
