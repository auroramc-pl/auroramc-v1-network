package quests

import org.bukkit.inventory.ItemFlag
import pl.auroramc.quests.objectives.block.BreakBlockObjective

import static org.bukkit.Material.STONE
import static org.bukkit.Material.WOODEN_SWORD
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
    key {
        withName("test_quest_3")
    }
    icon {
        type(WOODEN_SWORD)
        lore("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Początkujący gracz")
        flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
    }
    objectives {
        objective {
            key {
                withName("break_block")
            }
            typeOfObjective(BreakBlockObjective.class)
            type(STONE)
            goal(8, 32)
        }
    }
    weight(3)
}