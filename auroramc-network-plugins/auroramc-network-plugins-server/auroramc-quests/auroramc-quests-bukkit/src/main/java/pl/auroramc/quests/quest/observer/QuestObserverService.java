package pl.auroramc.quests.quest.observer;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

class QuestObserverService implements QuestObserverFacade {

  private final Logger logger;
  private final UserFacade userFacade;
  private final QuestObserverRepository questObserverRepository;
  private final LoadingCache<UUID, QuestObserver> questObserverByUniqueId;

  public QuestObserverService(
      final Logger logger,
      final UserFacade userFacade,
      final QuestObserverRepository questObserverRepository
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.questObserverRepository = questObserverRepository;
    this.questObserverByUniqueId = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
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

    return userFacade.getUserByUniqueId(uniqueId)
        .thenApply(User::getId)
        .thenApply(userId -> {
          final QuestObserver newQuestObserver = new QuestObserver(userId, null);
          createQuestObserver(newQuestObserver);
          return newQuestObserver;
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public void createQuestObserver(final QuestObserver questObserver) {
    runAsync(() -> questObserverRepository.createQuestObserver(questObserver))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public void updateQuestObserver(final QuestObserver questObserver) {
    runAsync(() -> questObserverRepository.updateQuestObserver(questObserver))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
