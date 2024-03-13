package pl.auroramc.quests.quest.observer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface QuestObserverFacade {

  QuestObserver findQuestObserverByUserUniqueId(final UUID userUniqueId);

  CompletableFuture<QuestObserver> resolveQuestObserverByUserUniqueId(final UUID userUniqueId);

  void createQuestObserver(final QuestObserver questObserver);

  void updateQuestObserver(final QuestObserver questObserver);
}
