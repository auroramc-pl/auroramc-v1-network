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
        lore("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Początkujący podróżnik")
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