package bounties

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

import static pl.auroramc.bounties.bounty.BountyDsl.bounty

bounty {
    key {
        name("day_2")
    }
    icon {
        lore(
                "<gray>Nagroda:",
                "<dark_gray>* <white>\$250",
                "<dark_gray>* <white>4 sztabki neterytu"
        )
    }
    day(2)
    rewards {
        item(new ItemStack(Material.NETHERITE_INGOT, 4))
        exec("eco add <target> 1 250")
    }
}