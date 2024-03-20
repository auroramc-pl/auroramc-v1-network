package pl.auroramc.quests.quest;

public final class QuestIndexFactory {

  private QuestIndexFactory() {}

  public static QuestIndex getQuestIndex() {
    return new MapBasedQuestIndex();
  }
}
