package pl.auroramc.quests.quest;

import static java.util.Comparator.comparingLong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class MapBasedQuestIndex implements QuestIndex {

  private final Map<Long, Quest> questIdToQuest;

  MapBasedQuestIndex() {
    this.questIdToQuest = new HashMap<>();
  }

  @Override
  public void indexQuests(final List<Quest> bunchOfQuests) {
    bunchOfQuests.forEach(quest -> questIdToQuest.put(quest.getKey().getId(), quest));
  }

  @Override
  public Quest resolveQuest(final Long questId) {
    return questIdToQuest.get(questId);
  }

  @Override
  public List<Quest> resolveQuests(final List<Long> bunchOfQuestIds) {
    return bunchOfQuestIds.stream()
        .map(questIdToQuest::get)
        .toList();
  }

  @Override
  public List<Quest> resolveQuests() {
    return questIdToQuest.entrySet().stream()
        .sorted(comparingLong(Entry::getKey))
        .map(Entry::getValue)
        .toList();
  }
}
