package pl.auroramc.quests.objective;

import static pl.auroramc.quests.objective.ObjectiveUtils.aggregateObjectives;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestController;

public abstract class ObjectiveHandlerDelegate<T extends Objective<?>, E extends Event>
    implements ObjectiveHandler<T, E>, Listener {

  private final QuestController questController;

  protected ObjectiveHandlerDelegate(final QuestController questController) {
    this.questController = questController;
  }

  protected void delegateObjectiveGoal(
      final E event, final UUID uniqueId, final Class<T> objectiveType) {
    final List<Quest> assignedQuests = questController.getAssignedQuestsByUserUniqueId(uniqueId);
    final Map<Quest, List<T>> questToObjectives = aggregateObjectives(assignedQuests, objectiveType);
    for (final Map.Entry<Quest, List<T>> questToObjective : questToObjectives.entrySet()) {
      final Quest quest = questToObjective.getKey();
      for (final T objective : questToObjective.getValue()) {
        validateObjectiveGoal(quest, objective, event);
      }
    }
  }
}
