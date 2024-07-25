package spawners

import org.bukkit.Material
import org.bukkit.entity.EntityType

import static pl.auroramc.spawners.spawner.SpawnerDsl.spawners
import static spawners.SpawnerUtils.getDisplayOf

spawners {
    spawner {
        icon(getDisplayOf(Material.ZOMBIE_SPAWN_EGG, "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Spawner zombie"))
        price(BigDecimal.valueOf(50_000))
        creatureType(EntityType.ZOMBIE)
    }
    spawner {
        icon(getDisplayOf(Material.SKELETON_SPAWN_EGG, "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Spawner szkieletów"))
        price(BigDecimal.valueOf(50_000))
        creatureType(EntityType.SKELETON)
    }
    spawner {
        icon(getDisplayOf(Material.IRON_GOLEM_SPAWN_EGG, "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Spawner golemów"))
        price(BigDecimal.valueOf(150_000))
        creatureType(EntityType.IRON_GOLEM)
    }
}