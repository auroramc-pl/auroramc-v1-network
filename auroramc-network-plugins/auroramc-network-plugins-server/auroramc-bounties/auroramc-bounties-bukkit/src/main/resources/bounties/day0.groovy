package bounties

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

import static pl.auroramc.bounties.bounty.BountyDsl.bounty

bounty {
    key {
        withName("day_0")
    }
    icon {
        lore(
                "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nagroda:",
                "<#7c5058>* <#f4a9ba>\$100",
                "<#7c5058>* <#f4a9ba>4 diamenty"
        )
        flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
    }
    day(0)
    rewards {
        item(new ItemStack(Material.DIAMOND, 4))
        exec("eco add <target> 1 100")
    }
}