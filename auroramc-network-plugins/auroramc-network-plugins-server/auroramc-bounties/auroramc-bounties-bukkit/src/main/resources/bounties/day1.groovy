package bounties

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

import static pl.auroramc.bounties.bounty.BountyDsl.bounty

bounty {
    key {
        withName("day_1")
    }
    icon {
        lore(
                "<gray>Nagroda:",
                "<dark_gray>* <white>\$175",
                "<dark_gray>* <white>4 emeraldy"
        )
    }
    day(1)
    rewards {
        item(new ItemStack(Material.EMERALD, 4))
        exec("eco add <target> 1 175")
    }
}