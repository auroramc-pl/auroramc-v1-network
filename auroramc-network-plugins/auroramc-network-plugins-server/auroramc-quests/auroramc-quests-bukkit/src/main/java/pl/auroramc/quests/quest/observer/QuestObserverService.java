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
  private final LoadingCache<UUID, QuestObserver> questObserverByUserUniqueId;

  public QuestObserverService(
      final Logger logger,
      final UserFacade userFacade,
      final QuestObserverRepository questObserverRepository
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.questObserverRepository = questObserverRepository;
    this.questObserverByUserUniqueId = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
        .build(questObserverRepository::findQuestObserverByUserUniqueId);
  }

  @Override
  public QuestObserver findQuestObserverByUserUniqueId(final UUID userUniqueId) {
    return questObserverByUserUniqueId.get(userUniqueId);
  }

  @Override
  public CompletableFuture<QuestObserver> resolveQuestObserverByUserUniqueId(final UUID userUniqueId) {
    final QuestObserver questObserver = findQuestObserverByUserUniqueId(userUniqueId);
    if (questObserver != null) {
      return completedFuture(questObserver);
    }

    return userFacade.getUserByUniqueId(userUniqueId)
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
