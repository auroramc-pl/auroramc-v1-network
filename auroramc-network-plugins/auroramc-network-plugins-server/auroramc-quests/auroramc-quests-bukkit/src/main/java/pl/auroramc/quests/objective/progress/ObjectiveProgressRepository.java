package pl.auroramc.quests.objective.progress;

import java.util.List;

interface ObjectiveProgressRepository {

  List<ObjectiveProgress> getObjectiveProgresses(
      final ObjectiveProgressCompositeKey objectiveProgressesKey);

  ObjectiveProgress getObjectiveProgress(final ObjectiveProgressKey objectiveProgressKey);

  void createObjectiveProgress(final ObjectiveProgress objectiveProgress);

  void updateObjectiveProgress(final ObjectiveProgress objectiveProgress);

  void deleteObjectiveProgressByUserIdAndQuestId(final Long userId, final Long questId);
}
