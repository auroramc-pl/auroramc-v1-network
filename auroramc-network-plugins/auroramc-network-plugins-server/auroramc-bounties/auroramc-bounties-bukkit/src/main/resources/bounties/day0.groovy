package bounties

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

import static pl.auroramc.bounties.bounty.BountyDsl.bounty

bounty {
    key {
        name("day_0")
    }
    icon {
        lore(
                "<gray>Nagroda:",
                "<dark_gray>* <white>\$100",
                "<dark_gray>* <white>4 diamenty"
        )
    }
    day(0)
    rewards {
        item(new ItemStack(Material.DIAMOND, 4))
        exec("eco add <target> 1 100")
    }
}