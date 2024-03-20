package pl.auroramc.quests.objectives.block;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.auroramc.quests.objective.ObjectiveHandlerDelegate;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestController;

public class PlaceBlockObjectiveHandler
    extends ObjectiveHandlerDelegate<PlaceBlockObjective, BlockPlaceEvent> {

  private final ObjectiveProgressController objectiveProgressController;

  public PlaceBlockObjectiveHandler(
      final QuestController questController,
      final ObjectiveProgressController objectiveProgressController) {
    super(questController);
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public void validateObjectiveGoal(
      final Quest quest, final PlaceBlockObjective objective, final BlockPlaceEvent event) {
    if (objective.getType() != event.getBlock().getType()) {
      return;
    }

    objectiveProgressController.processObjectiveGoal(
        event.getPlayer().getUniqueId(), quest, objective);
  }

  @EventHandler
  public void delegateObjectiveGoal(final BlockPlaceEvent event) {
    super.delegateObjectiveGoal(event, event.getPlayer().getUniqueId(), PlaceBlockObjective.class);
  }
}
