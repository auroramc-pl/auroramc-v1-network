package pl.auroramc.spawners.spawner;

import static java.util.Objects.requireNonNull;
import static org.bukkit.Material.SPAWNER;
import static org.bukkit.persistence.PersistentDataType.STRING;
import static pl.auroramc.messages.message.decoration.MessageDecorations.NO_CURSIVE;
import static pl.auroramc.spawners.spawner.SpawnerMessageSourcePaths.CREATURE_TYPE_PATH;

import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.integrations.item.ItemStackBuilder;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

final class SpawnerUtils {

  private SpawnerUtils() {}

  static ItemStack getSpawnerItem(
      final NamespacedKey spawnedCreatureKey,
      final SpawnerMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EntityType creatureType) {
    return ItemStackBuilder.newBuilder(SPAWNER)
        .displayName(
            messageCompiler
                .compile(
                    messageSource.spawnerDisplayName.placeholder(CREATURE_TYPE_PATH, creatureType),
                    NO_CURSIVE)
                .getComponent())
        .manipulate(
            Objects::nonNull,
            creatureType,
            builder ->
                builder.data(spawnedCreatureKey, STRING, requireNonNull(creatureType).name()))
        .build();
  }
}
