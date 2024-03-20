package pl.auroramc.quests.objective.progress;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.quests.objective.Objective;

class ObjectiveProgressService implements ObjectiveProgressFacade {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private static final int INITIAL_OBJECTIVE_DATA = 0;
  private final Logger logger;
  private final ObjectiveProgressRepository objectiveProgressRepository;
  private final LoadingCache<ObjectiveProgressKey, ObjectiveProgress>
      objectiveProgressKeyToObjectiveProgress;
  private final LoadingCache<ObjectiveProgressesKey, List<ObjectiveProgress>>
      objectiveProgressesKeyToObjectiveProgresses;

  ObjectiveProgressService(
      final Logger logger, final ObjectiveProgressRepository objectiveProgressRepository) {
    this.logger = logger;
    this.objectiveProgressRepository = objectiveProgressRepository;
    this.objectiveProgressKeyToObjectiveProgress =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .build(objectiveProgressRepository::getObjectiveProgress);
    this.objectiveProgressesKeyToObjectiveProgresses =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(20))
            .build(objectiveProgressRepository::getObjectiveProgresses);
  }

  @Override
  public List<ObjectiveProgress> getObjectiveProgresses(final Long userId, final Long questId) {
    return objectiveProgressesKeyToObjectiveProgresses.get(
        new ObjectiveProgressesKey(userId, questId));
  }

  @Override
  public ObjectiveProgress getObjectiveProgress(
      final Long userId, final Long questId, final Long objectiveId) {
    return objectiveProgressKeyToObjectiveProgress.get(
        new ObjectiveProgressKey(userId, questId, objectiveId));
  }

  @Override
  public ObjectiveProgress resolveObjectiveProgress(
      final Long userId, final Long questId, final Long objectiveId, final int goal) {
    final ObjectiveProgress objectiveProgress = getObjectiveProgress(userId, questId, objectiveId);
    if (objectiveProgress != null) {
      return objectiveProgress;
    }

    final ObjectiveProgress newObjectiveProgress =
        new ObjectiveProgress(userId, questId, objectiveId, INITIAL_OBJECTIVE_DATA, goal);
    createObjectiveProgress(newObjectiveProgress);
    return newObjectiveProgress;
  }

  @Override
  public void createObjectiveProgress(final ObjectiveProgress objectiveProgress) {
    objectiveProgressKeyToObjectiveProgress.put(
        getObjectiveProgressKey(objectiveProgress), objectiveProgress);
    runAsyncWithExceptionDelegation(
        () -> objectiveProgressRepository.createObjectiveProgress(objectiveProgress));
  }

  private ObjectiveProgressKey getObjectiveProgressKey(final ObjectiveProgress objectiveProgress) {
    return new ObjectiveProgressKey(
        objectiveProgress.getUserId(),
        objectiveProgress.getQuestId(),
        objectiveProgress.getObjectiveId());
  }

  @Override
  public CompletableFuture<Void> updateObjectiveProgress(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    updateObjectiveProgress0(objectiveProgress);
    if (objectiveProgress.getData() % objective.getSaveInterval() != 0) {
      return EMPTY_FUTURE;
    }

    return runAsyncWithExceptionDelegation(
            () -> objectiveProgressRepository.updateObjectiveProgress(objectiveProgress))
        .thenAccept(
            state ->
                objectiveProgressesKeyToObjectiveProgresses.invalidate(
                    new ObjectiveProgressesKey(
                        objectiveProgress.getUserId(), objectiveProgress.getQuestId())));
  }

  private void updateObjectiveProgress0(final ObjectiveProgress objectiveProgress) {
    final List<ObjectiveProgress> objectiveProgresses =
        objectiveProgressesKeyToObjectiveProgresses.getIfPresent(
            new ObjectiveProgressesKey(
                objectiveProgress.getUserId(), objectiveProgress.getQuestId()));
    if (objectiveProgresses != null) {
      for (final ObjectiveProgress objectiveProgress0 : objectiveProgresses) {
        if (objectiveProgress0.getObjectiveId().equals(objectiveProgress.getObjectiveId())) {
          objectiveProgresses.set(
              objectiveProgresses.indexOf(objectiveProgress0), objectiveProgress);
          break;
        }
      }
    }
  }

  @Override
  public void deleteObjectiveProgressByUserIdAndQuestId(final Long userId, final Long questId) {
    runAsyncWithExceptionDelegation(
        () ->
            objectiveProgressRepository.deleteObjectiveProgressByUserIdAndQuestId(userId, questId));
  }

  private CompletableFuture<Void> runAsyncWithExceptionDelegation(final Runnable runnable) {
    return runAsync(runnable)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
