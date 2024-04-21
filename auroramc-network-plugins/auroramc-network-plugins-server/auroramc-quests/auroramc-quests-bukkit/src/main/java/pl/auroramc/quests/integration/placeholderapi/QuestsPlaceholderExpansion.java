package pl.auroramc.quests.integration.placeholderapi;

import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveController;
import pl.auroramc.quests.objective.progress.ObjectiveProgress;
import pl.auroramc.quests.objective.progress.ObjectiveProgressController;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.QuestIndex;
import pl.auroramc.quests.quest.observer.QuestObserver;
import pl.auroramc.quests.quest.observer.QuestObserverFacade;
import pl.auroramc.registry.user.UserFacade;

class QuestsPlaceholderExpansion extends PlaceholderExpansionDelegate {

  private final UserFacade userFacade;
  private final QuestIndex questIndex;
  private final QuestObserverFacade questObserverFacade;
  private final ObjectiveController objectiveController;
  private final ObjectiveProgressController objectiveProgressController;

  public QuestsPlaceholderExpansion(
      final Plugin plugin,
      final UserFacade userFacade,
      final QuestIndex questIndex,
      final QuestObserverFacade questObserverFacade,
      final ObjectiveController objectiveController,
      final ObjectiveProgressController objectiveProgressController) {
    super(plugin);
    this.userFacade = userFacade;
    this.questIndex = questIndex;
    this.questObserverFacade = questObserverFacade;
    this.objectiveController = objectiveController;
    this.objectiveProgressController = objectiveProgressController;
  }

  @Override
  public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
    if (player == null) {
      return null;
    }

    final QuestObserver questObserver =
        questObserverFacade.resolveQuestObserverByUniqueId(player.getUniqueId()).join();
    if (questObserver.getQuestId() == null) {
      return null;
    }

    final Quest quest = questIndex.getQuestById(questObserver.getQuestId());
    if (quest == null) {
      return null;
    }

    return switch (params) {
      case "observed_quest":
        yield quest.getKey().getName();
      case "observed_quest_objectives":
        yield userFacade
            .getUserByUniqueId(player.getUniqueId())
            .thenApply(user -> objectiveProgressController.getUncompletedObjectives(user, quest))
            .thenApply(this::aggregateQuestObjectives)
            .join();
      default:
        yield null;
    };
  }

  private String aggregateQuestObjectives(
      final Map<Objective<?>, ObjectiveProgress> objectivesToObjectiveProgresses) {
    return objectiveController.getQuestObjectivesTemplate(
        List.copyOf(objectivesToObjectiveProgresses.keySet()), objectivesToObjectiveProgresses);
  }
}
