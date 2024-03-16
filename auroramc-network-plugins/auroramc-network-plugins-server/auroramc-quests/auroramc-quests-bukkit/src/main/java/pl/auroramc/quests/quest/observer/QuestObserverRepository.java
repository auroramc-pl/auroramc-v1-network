package pl.auroramc.quests.quest.observer;

import java.util.UUID;

interface QuestObserverRepository {

  QuestObserver findQuestObserverByUniqueId(final UUID uniqueId);

  void createQuestObserver(final QuestObserver questObserver);

  void updateQuestObserver(final QuestObserver questObserver);
}
