package pl.auroramc.quests.quest;

import java.util.List;

public interface QuestIndex {

  void indexQuests(final List<Quest> bunchOfQuests);

  Quest resolveQuest(final Long questId);

  List<Quest> resolveQuests(final List<Long> bunchOfQuestIds);

  List<Quest> resolveQuests();
}
