package bounties

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

import static pl.auroramc.bounties.bounty.BountyDsl.bounty

bounty {
    key {
        withName("day_2")
    }
    icon {
        lore(
                "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nagroda:",
                "<#7c5058>* <#f4a9ba>\$250",
                "<#7c5058>* <#f4a9ba>4 sztabki neterytu"
        )
        flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
    }
    day(2)
    rewards {
        item(new ItemStack(Material.NETHERITE_INGOT, 4))
        exec("eco add <target> 1 250")
    }
}