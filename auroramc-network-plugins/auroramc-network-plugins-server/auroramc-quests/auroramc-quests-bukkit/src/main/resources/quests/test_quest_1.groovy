package quests

import org.bukkit.inventory.ItemStack
import pl.auroramc.quests.objectives.block.BreakBlockObjective
import pl.auroramc.quests.objectives.block.PlaceBlockObjective

import static org.bukkit.Material.DIAMOND
import static org.bukkit.Material.DIRT
import static org.bukkit.Material.STONE
import static org.bukkit.Material.WOODEN_PICKAXE
import static pl.auroramc.quests.quest.QuestDsl.quest

quest {
  key {
    name("test_quest_1")
  }
  icon {
    type(WOODEN_PICKAXE)
    lore("<gray>Lorem ipsum dolor sit amet, consectetur")
    displayName("<gray>Początkujący górnik")
  }
  objectives {
    objective {
      key {
        name("place_block_1")
      }
      typeOfObjective(PlaceBlockObjective.class)
      type(STONE)
      goal(8)
    }
    objective {
      key {
        name("place_block_2")
      }
      typeOfObjective(PlaceBlockObjective.class)
      type(DIRT)
      goal(4)
    }
    objective {
      key {
        name("break_block")
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