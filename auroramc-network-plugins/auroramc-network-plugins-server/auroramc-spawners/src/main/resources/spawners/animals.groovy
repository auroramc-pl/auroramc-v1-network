package spawners

import org.bukkit.Material
import org.bukkit.entity.EntityType
import pl.auroramc.integrations.item.ItemStackBuilder

import static pl.auroramc.spawners.spawner.SpawnerDsl.spawners
import static spawners.SpawnerUtils.getDisplayOf

spawners {
    spawner {
        icon(getDisplayOf(Material.SHEEP_SPAWN_EGG, "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Spawner owiec"))
        price(BigDecimal.valueOf(10_000))
        creatureType(EntityType.SHEEP)
    }
    spawner {
        icon(getDisplayOf(Material.CHICKEN_SPAWN_EGG, "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Spawner kur"))
        price(BigDecimal.valueOf(15_000))
        creatureType(EntityType.CHICKEN)
    }
    spawner {
        icon(getDisplayOf(Material.COW_SPAWN_EGG, "<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Spawner krów"))
        price(BigDecimal.valueOf(30_000))
        creatureType(EntityType.COW)
    }
}

class SpawnerUtils {

    static def getDisplayOf(final Material type, final String displayName) {
        return ItemStackBuilder.newBuilder(type)
                .with {
                    it.displayName(displayName)
                }
                .build()
    }
}