package pl.auroramc.commons.config.serdes.juliet;

import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.SerdesRegistry;
import org.jetbrains.annotations.NotNull;

public class SerdesJuliet implements OkaeriSerdesPack {

  @Override
  public void register(final @NotNull SerdesRegistry registry) {
    registry.register(new HikariConfigSerializer());
  }
}
