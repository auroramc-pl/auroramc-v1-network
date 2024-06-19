package pl.auroramc.spawners.spawner;

import java.math.BigDecimal;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public record Spawner(ItemStack icon, BigDecimal price, EntityType creatureType) {

  public static SpawnerBuilder newBuilder() {
    return new SpawnerBuilder();
  }
}
