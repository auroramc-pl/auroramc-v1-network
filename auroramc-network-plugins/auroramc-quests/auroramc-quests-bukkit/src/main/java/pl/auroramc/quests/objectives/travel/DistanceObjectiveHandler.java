package pl.auroramc.quests.objectives.travel;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.auroramc.quests.objective.ObjectiveHandlerDelegate;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestController;

public class DistanceObjectiveHandler
    extends ObjectiveHandlerDelegate<DistanceObjective, PlayerMoveEvent> {

  private final ObjectiveProgressController objectiveProgressController;

  public DistanceObjectiveHandler(
      final QuestController questController,
      final ObjectiveProgressController objectiveProgressController
  ) {
    super(questController);
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public void validateObjectiveGoal(
      final Quest quest, final DistanceObjective objective, final PlayerMoveEvent event
  ) {
    final boolean whetherIsMouseMovement =
        event.getFrom().getX() == event.getTo().getX() &&
        event.getFrom().getY() == event.getTo().getY() &&
        event.getFrom().getZ() == event.getTo().getZ();
    if (whetherIsMouseMovement) {
      return;
    }

    final boolean whetherIsBlockChanged =
        event.getFrom().getBlockX() != event.getTo().getBlockX() ||
        event.getFrom().getBlockY() != event.getTo().getBlockY() ||
        event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    if (whetherIsBlockChanged) {
      objectiveProgressController.processObjectiveGoal(event.getPlayer().getUniqueId(), quest, objective);
    }
  }

  @EventHandler
  public void delegateObjectiveGoal(final PlayerMoveEvent event) {
    super.delegateObjectiveGoal(event, event.getPlayer().getUniqueId(), DistanceObjective.class);
  }
}
