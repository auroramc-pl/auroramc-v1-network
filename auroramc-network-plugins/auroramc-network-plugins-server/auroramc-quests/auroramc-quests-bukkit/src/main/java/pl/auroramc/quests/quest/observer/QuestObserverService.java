package pl.auroramc.quests.quest.observer;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class QuestObserverService implements QuestObserverFacade {

  private final Scheduler scheduler;
  private final UserFacade userFacade;
  private final QuestObserverRepository questObserverRepository;
  private final LoadingCache<UUID, QuestObserver> questObserverByUniqueId;

  public QuestObserverService(
      final Scheduler scheduler,
      final UserFacade userFacade,
      final QuestObserverRepository questObserverRepository) {
    this.scheduler = scheduler;
    this.userFacade = userFacade;
    this.questObserverRepository = questObserverRepository;
    this.questObserverByUniqueId =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterAccess(ofSeconds(20))
            .build(questObserverRepository::findQuestObserverByUniqueId);
  }

  @Override
  public QuestObserver findQuestObserverByUniqueId(final UUID uniqueId) {
    return questObserverByUniqueId.get(uniqueId);
  }

  @Override
  public CompletableFuture<QuestObserver> resolveQuestObserverByUniqueId(final UUID uniqueId) {
    final QuestObserver questObserver = findQuestObserverByUniqueId(uniqueId);
    if (questObserver != null) {
      return completedFuture(questObserver);
    }

    return userFacade
        .getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenApply(
            userId -> {
              final QuestObserver newQuestObserver = new QuestObserver(userId, null);
              createQuestObserver(newQuestObserver);
              return newQuestObserver;
            })
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public void createQuestObserver(final QuestObserver questObserver) {
    scheduler
        .run(ASYNC, () -> questObserverRepository.createQuestObserver(questObserver))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public void updateQuestObserver(final QuestObserver questObserver) {
    scheduler
        .run(ASYNC, () -> questObserverRepository.updateQuestObserver(questObserver))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
