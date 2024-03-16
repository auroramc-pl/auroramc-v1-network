package pl.auroramc.quests.quest.observer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface QuestObserverFacade {

  QuestObserver findQuestObserverByUniqueId(final UUID uniqueId);

  CompletableFuture<QuestObserver> resolveQuestObserverByUniqueId(final UUID uniqueId);

  void createQuestObserver(final QuestObserver questObserver);

  void updateQuestObserver(final QuestObserver questObserver);
}
