package pl.auroramc.quests.objective.progress;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.CompletableFutureUtils.NIL;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;
import pl.auroramc.quests.objective.Objective;

class ObjectiveProgressService implements ObjectiveProgressFacade {

  private static final int INITIAL_OBJECTIVE_DATA = 0;
  private final Scheduler scheduler;
  private final ObjectiveProgressRepository objectiveProgressRepository;
  private final LoadingCache<ObjectiveProgressKey, ObjectiveProgress>
      objectiveProgressKeyToObjectiveProgress;
  private final LoadingCache<ObjectiveProgressCompositeKey, List<ObjectiveProgress>>
      objectiveProgressesKeyToObjectiveProgresses;

  ObjectiveProgressService(
      final Scheduler scheduler, final ObjectiveProgressRepository objectiveProgressRepository) {
    this.scheduler = scheduler;
    this.objectiveProgressRepository = objectiveProgressRepository;
    this.objectiveProgressKeyToObjectiveProgress =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(30))
            .build(objectiveProgressRepository::getObjectiveProgress);
    this.objectiveProgressesKeyToObjectiveProgresses =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(20))
            .build(objectiveProgressRepository::getObjectiveProgresses);
  }

  @Override
  public List<ObjectiveProgress> getObjectiveProgresses(final Long userId, final Long questId) {
    return objectiveProgressesKeyToObjectiveProgresses.get(
        new ObjectiveProgressCompositeKey(userId, questId));
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
    scheduler
        .run(ASYNC, () -> objectiveProgressRepository.createObjectiveProgress(objectiveProgress))
        .thenAccept(
            state ->
                objectiveProgressKeyToObjectiveProgress.put(
                    getObjectiveProgressKey(objectiveProgress), objectiveProgress))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> updateObjectiveProgress(
      final Objective<?> objective, final ObjectiveProgress objectiveProgress) {
    updateObjectiveProgress0(objectiveProgress);
    if (objectiveProgress.getData() % objective.getSaveInterval() != 0) {
      return NIL;
    }

    return scheduler
        .run(ASYNC, () -> objectiveProgressRepository.updateObjectiveProgress(objectiveProgress))
        .thenAccept(
            state ->
                objectiveProgressesKeyToObjectiveProgresses.invalidate(
                    new ObjectiveProgressCompositeKey(
                        objectiveProgress.getUserId(), objectiveProgress.getQuestId())))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void updateObjectiveProgress0(final ObjectiveProgress objectiveProgress) {
    final List<ObjectiveProgress> objectiveProgresses =
        objectiveProgressesKeyToObjectiveProgresses.getIfPresent(
            new ObjectiveProgressCompositeKey(
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
    scheduler
        .run(
            ASYNC,
            () ->
                objectiveProgressRepository.deleteObjectiveProgressByUserIdAndQuestId(
                    userId, questId))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private ObjectiveProgressKey getObjectiveProgressKey(final ObjectiveProgress objectiveProgress) {
    return new ObjectiveProgressKey(
        objectiveProgress.getUserId(),
        objectiveProgress.getQuestId(),
        objectiveProgress.getObjectiveId());
  }
}
