package quests

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import pl.auroramc.quests.objectives.block.BreakBlockObjective
import pl.auroramc.quests.objectives.block.PlaceBlockObjective

import static org.bukkit.Material.*
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
    key {
        withName("test_quest_1")
    }
    icon {
        type(WOODEN_PICKAXE)
        lore("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Początkujący górnik")
        flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
    }
    objectives {
        objective {
            key {
                withName("place_block_1")
            }
            typeOfObjective(PlaceBlockObjective.class)
            type(STONE)
            goal(8)
        }
        objective {
            key {
                withName("place_block_2")
            }
            typeOfObjective(PlaceBlockObjective.class)
            type(DIRT)
            goal(4)
        }
        objective {
            key {
                withName("break_block")
            }
            typeOfObjective(BreakBlockObjective.class)
            type(STONE)
            goal(8)
        }
    }
    rewards {
        item(new ItemStack(DIAMOND, 4))
    }
    weight(1)
}