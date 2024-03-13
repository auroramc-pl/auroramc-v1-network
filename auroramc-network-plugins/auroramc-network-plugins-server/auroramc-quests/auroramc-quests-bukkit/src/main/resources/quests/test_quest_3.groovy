package quests

import pl.auroramc.quests.objectives.block.BreakBlockObjective

import static org.bukkit.Material.STONE
import static org.bukkit.Material.WOODEN_SWORD
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
  key {
    name("test_quest_3")
  }
  icon {
    type(WOODEN_SWORD)
    lore("<gray>Lorem ipsum dolor sit amet, consectetur")
    displayName("<gray>Początkujący gracz")
  }
  objectives {
    objective {
      key {
        name("break_block")
      }
      typeOfObjective(BreakBlockObjective.class)
      type(STONE)
      goal(8, 32)
    }
  }
  weight(3)
}