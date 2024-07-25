package quests

import org.bukkit.inventory.ItemFlag
import pl.auroramc.quests.objectives.travel.DistanceObjective

import static org.bukkit.Material.LEATHER_BOOTS
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
    key {
        withName("test_quest_4")
    }
    icon {
        type(LEATHER_BOOTS)
        lore("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Początkujący podróżnik")
        flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
    }
    objectives {
        objective {
            key {
                withName("distance")
            }
            typeOfObjective(DistanceObjective.class)
            type((byte) 0)
            goal(400, 600)
        }
    }
    weight(4)
}