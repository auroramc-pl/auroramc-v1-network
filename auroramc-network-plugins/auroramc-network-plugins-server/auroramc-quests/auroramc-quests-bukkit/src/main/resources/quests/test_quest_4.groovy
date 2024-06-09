package quests

import pl.auroramc.quests.objectives.travel.DistanceObjective

import static org.bukkit.Material.LEATHER_BOOTS
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
    key {
        name("test_quest_4")
    }
    icon {
        type(LEATHER_BOOTS)
        lore("<gray>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gray>Początkujący podróżnik")
    }
    objectives {
        objective {
            key {
                name("distance")
            }
            typeOfObjective(DistanceObjective.class)
            type((byte) 0)
            goal(400, 600)
        }
    }
    weight(4)
}