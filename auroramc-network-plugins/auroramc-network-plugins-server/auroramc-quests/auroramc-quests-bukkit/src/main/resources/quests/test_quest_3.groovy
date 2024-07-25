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
        lore("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Początkujący gracz")
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