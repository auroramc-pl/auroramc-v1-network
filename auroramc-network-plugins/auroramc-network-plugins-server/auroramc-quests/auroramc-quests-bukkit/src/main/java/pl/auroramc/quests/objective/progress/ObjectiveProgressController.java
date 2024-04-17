package pl.auroramc.quests.objective.progress;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;
import static org.bukkit.Bukkit.getPlayer;
import static pl.auroramc.commons.CompletableFutureUtils.NIL;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.quest.Quest;
import pl.auroramc.quests.quest.track.QuestTrackController;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class ObjectiveProgressController {

  private final UserFacade userFacade;
  private final QuestTrackController questTrackController;
  private final ObjectiveProgressFacade objectiveProgressFacade;

  public ObjectiveProgressController(
      final UserFacade userFacade,
      final QuestTrackController questTrackController,
      final ObjectiveProgressFacade objectiveProgressFacade) {
    this.userFacade = userFacade;
    this.questTrackController = questTrackController;
    this.objectiveProgressFacade = objectiveProgressFacade;
  }

  public void processObjectiveGoal(
      final UUID uniqueId, final Quest quest, final Objective<?> objective) {
    final Player player = checkNotNull(getPlayer(uniqueId));

    final List<ObjectiveRequirement> requirements = objective.getRequirements();
    if (requirements.isEmpty()
        || requirements.stream().allMatch(requirement -> requirement.isValid(player))) {
      processObjectiveGoal0(uniqueId, quest, objective);
    }
  }

  private void processObjectiveGoal0(
      final UUID uniqueId, final Quest quest, final Objective<?> objective) {
    userFacade
        .getUserByUniqueId(uniqueId)
        .thenCompose(user -> processObjectiveGoal(user, quest, objective))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  public CompletableFuture<Void> processObjectiveGoal(
      final User user, final Quest quest, final Objective<?> objective) {
    final ObjectiveProgress objectiveProgress =
        objectiveProgressFacade.resolveObjectiveProgress(
            user.getId(),
            quest.getKey().getId(),
            objective.getKey().getId(),
            objective.getGoalResolver().resolveGoal());
    if (isObjectiveCompleted(objectiveProgress)) {
      completeQuestIfAllObjectivesAreCompleted(user, quest);
      return NIL;
    }

    objectiveProgress.setData(objectiveProgress.getData() + 1);
    return objectiveProgressFacade
        .updateObjectiveProgress(objective, objectiveProgress)
        .thenAccept(
            state -> completeQuestIfAllObjectivesAreCompleted(user, quest, objectiveProgress))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  public Map<Objective<?>, ObjectiveProgress> getUncompletedObjectives(
      final User user, final Quest quest) {
    return objectiveProgressFacade
        .getObjectiveProgresses(user.getId(), quest.getKey().getId())
        .stream()
        .filter(not(this::isObjectiveCompleted))
        .collect(
            toMap(
                objectiveProgress ->
                    quest.getObjectiveByObjectiveId(objectiveProgress.getObjectiveId()),
                identity()));
  }

  private void completeQuestIfAllObjectivesAreCompleted(
      final User user, final Quest quest, final ObjectiveProgress objectiveProgress) {
    if (isObjectiveCompleted(objectiveProgress)) {
      completeQuestIfAllObjectivesAreCompleted(user, quest);
    }
  }

  private void completeQuestIfAllObjectivesAreCompleted(final User user, final Quest quest) {
    if (getSumOfCompletedObjectives(user, quest) >= quest.getObjectives().size()) {
      questTrackController.completeQuest(user, quest);
    }
  }

  private long getSumOfCompletedObjectives(final User user, final Quest quest) {
    return objectiveProgressFacade
        .getObjectiveProgresses(user.getId(), quest.getKey().getId())
        .stream()
        .filter(this::isObjectiveCompleted)
        .count();
  }

  private boolean isObjectiveCompleted(final ObjectiveProgress objectiveProgress) {
    return objectiveProgress.getData() >= objectiveProgress.getGoal();
  }
}
