package pl.auroramc.quests.objectives.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import pl.auroramc.quests.objective.ObjectiveHandlerDelegate;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestController;

public class BreakBlockObjectiveHandler extends
    ObjectiveHandlerDelegate<BreakBlockObjective, BlockBreakEvent> {

  private final ObjectiveProgressController objectiveProgressController;

  public BreakBlockObjectiveHandler(
      final QuestController questController,
      final ObjectiveProgressController objectiveProgressController
  ) {
    super(questController);
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public void validateObjectiveGoal(
      final Quest quest, final BreakBlockObjective objective, final BlockBreakEvent event) {
    if (objective.getType() != event.getBlock().getType()) {
      return;
    }

    objectiveProgressController.processObjectiveGoal(event.getPlayer().getUniqueId(), quest, objective);
  }

  @EventHandler
  public void delegateObjectiveGoal(final BlockBreakEvent event) {
    super.delegateObjectiveGoal(event, event.getPlayer().getUniqueId(), BreakBlockObjective.class);
  }
}
