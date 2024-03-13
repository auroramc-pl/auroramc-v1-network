package pl.auroramc.quests.quest.observer;

import java.util.UUID;

interface QuestObserverRepository {

  QuestObserver findQuestObserverByUserUniqueId(final UUID userUniqueId);

  void createQuestObserver(final QuestObserver questObserver);

  void updateQuestObserver(final QuestObserver questObserver);
}
