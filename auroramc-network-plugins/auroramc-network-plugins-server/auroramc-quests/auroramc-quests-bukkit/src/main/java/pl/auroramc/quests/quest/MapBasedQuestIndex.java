package pl.auroramc.quests.quest;

import static java.util.Comparator.comparingLong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class MapBasedQuestIndex implements QuestIndex {

  private final Map<Long, Quest> questsByIds;
  private final Map<String, Quest> questsByNames;

  MapBasedQuestIndex() {
    this.questsByIds = new HashMap<>();
    this.questsByNames = new HashMap<>();
  }

  @Override
  public void indexQuests(final List<Quest> bunchOfQuests) {
    bunchOfQuests.forEach(this::persistQuest);
  }

  @Override
  public Quest getQuestById(final Long questId) {
    return questsByIds.get(questId);
  }

  @Override
  public Quest getQuestByName(final String name) {
    return questsByNames.get(name);
  }

  @Override
  public List<Quest> getQuestsByIds(final List<Long> bunchOfQuestIds) {
    return bunchOfQuestIds.stream().map(questsByIds::get).toList();
  }

  @Override
  public List<Quest> getQuests() {
    return questsByIds.entrySet().stream()
        .sorted(comparingLong(Entry::getKey))
        .map(Entry::getValue)
        .toList();
  }

  private void persistQuest(final Quest quest) {
    questsByIds.put(quest.getKey().getId(), quest);
    questsByNames.put(quest.getKey().getName(), quest);
  }
}
