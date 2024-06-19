package pl.auroramc.spawners.spawner;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static org.bukkit.Material.STONE;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.math.BigDecimal;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.integrations.item.ItemStackBuilder;

public final class SpawnerBuilder {

  private ItemStack icon;
  private BigDecimal price;
  private EntityType creatureType;

  SpawnerBuilder() {

  }

  public SpawnerBuilder icon(final ItemStack icon) {
    this.icon = icon;
    return this;
  }

  public SpawnerBuilder icon(final @DelegatesTo(value = ItemStackBuilder.class) Closure<?> closure) {
    final ItemStackBuilder itemStackBuilder = ItemStackBuilder.newBuilder(STONE);
    closure.setDelegate(itemStackBuilder);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return icon(itemStackBuilder.build());
  }

  public SpawnerBuilder price(final BigDecimal price) {
    this.price = price;
    return this;
  }

  public SpawnerBuilder creatureType(final EntityType creatureType) {
    this.creatureType = creatureType;
    return this;
  }

  public Spawner build() {
    return new Spawner(icon, price, creatureType);
  }
}
