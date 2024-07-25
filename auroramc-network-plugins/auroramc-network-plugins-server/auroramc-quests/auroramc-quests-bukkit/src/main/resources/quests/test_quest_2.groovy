package quests

import org.bukkit.inventory.ItemFlag
import pl.auroramc.quests.objectives.block.BreakBlockObjective
import pl.auroramc.quests.objectives.travel.DistanceObjective

import static org.bukkit.Material.*
import static pl.auroramc.quests.objective.requirement.ObjectiveRequirementFactory.heldItem
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
    key {
        withName("test_quest_2")
    }
    icon {
        type(WOODEN_AXE)
        lore("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894>Lorem ipsum dolor sit amet, consectetur")
        displayName("<gradient:#f5c894:#f6d4a2:#f9e2b4:#f6d4a2:#f5c894><bold>Początkujący drwal")
        flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
    }
    objectives {
        objective {
            key {
                withName("break_block")
            }
            typeOfObjective(BreakBlockObjective.class)
            type(OAK_LOG)
            goal(16)
            requirements {
                requirement(heldItem(IRON_AXE))
            }
        }
        objective {
            key {
                withName("distance_traveled_with_axe")
            }
            typeOfObjective(DistanceObjective.class)
            type((byte) 1)
            goal(1000, 2000)
            saveInterval(20)
            requirements {
                requirement(heldItem(IRON_AXE))
            }
        }
    }
    rewards {
        exec("eco add <target> 1 100")
    }
    weight(2)
}