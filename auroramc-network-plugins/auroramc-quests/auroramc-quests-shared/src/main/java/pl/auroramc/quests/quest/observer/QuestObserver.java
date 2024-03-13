package pl.auroramc.quests.quest.observer;

public class QuestObserver {

  private final Long userId;
  private Long questId;

  public QuestObserver(final Long userId, final Long questId) {
    this.userId = userId;
    this.questId = questId;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getQuestId() {
    return questId;
  }

  public void setQuestId(final Long questId) {
    this.questId = questId;
  }
}
