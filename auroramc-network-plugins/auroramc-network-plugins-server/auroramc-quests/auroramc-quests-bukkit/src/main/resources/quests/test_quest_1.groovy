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
        lore("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Początkujący górnik")
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