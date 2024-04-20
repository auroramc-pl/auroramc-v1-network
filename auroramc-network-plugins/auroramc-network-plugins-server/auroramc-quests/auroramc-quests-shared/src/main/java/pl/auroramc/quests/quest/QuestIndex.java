package pl.auroramc.quests.quest;

import java.util.List;

public interface QuestIndex {

  void indexQuests(final List<Quest> bunchOfQuests);

  Quest getQuestById(final Long questId);

  Quest getQuestByName(final String name);

  List<Quest> getQuestsByIds(final List<Long> bunchOfQuestIds);

  List<Quest> getQuests();
}
