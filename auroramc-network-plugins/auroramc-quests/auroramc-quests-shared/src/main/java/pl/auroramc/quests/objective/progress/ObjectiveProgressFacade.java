package pl.auroramc.quests.objective.progress;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.quests.objective.Objective;

public interface ObjectiveProgressFacade {

  List<ObjectiveProgress> getObjectiveProgresses(
      final Long userId, final Long questId
  );

  ObjectiveProgress getObjectiveProgress(
      final Long userId, final Long questId, final Long objectiveId
  );

  ObjectiveProgress resolveObjectiveProgress(
      final Long userId, final Long questId, final Long objectiveId, final int goal
  );

  void createObjectiveProgress(final ObjectiveProgress objectiveProgress);

  CompletableFuture<Void> updateObjectiveProgress(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress
  );

  void deleteObjectiveProgressByUserIdAndQuestId(final Long userId, final Long questId);
}
