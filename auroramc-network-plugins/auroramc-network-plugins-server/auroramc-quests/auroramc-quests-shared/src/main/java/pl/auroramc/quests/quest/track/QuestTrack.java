package pl.auroramc.quests.quest.track;

import pl.auroramc.quests.quest.QuestState;

public class QuestTrack {

  private final Long userId;
  private final Long questId;
  private QuestState questState;
  private boolean completed;

  public QuestTrack(final Long userId, final Long questId, final QuestState questState) {
    this.userId = userId;
    this.questId = questId;
    this.questState = questState;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getQuestId() {
    return questId;
  }

  public QuestState getQuestState() {
    return questState;
  }

  public void setQuestState(final QuestState questState) {
    this.questState = questState;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(final boolean completed) {
    this.completed = completed;
  }
}
