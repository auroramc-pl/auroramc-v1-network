package pl.auroramc.quests.objective.progress;

public class ObjectiveProgress {

  private final Long userId;
  private final Long questId;
  private final Long objectiveId;
  private Integer data;
  private final Integer goal;

  public ObjectiveProgress(
      final Long userId,
      final Long questId,
      final Long objectiveId,
      final Integer data,
      final Integer goal
  ) {
    this.userId = userId;
    this.questId = questId;
    this.objectiveId = objectiveId;
    this.data = data;
    this.goal = goal;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getQuestId() {
    return questId;
  }

  public Long getObjectiveId() {
    return objectiveId;
  }

  public Integer getData() {
    return data;
  }

  public void setData(final Integer data) {
    this.data = data;
  }

  public Integer getGoal() {
    return goal;
  }
}
