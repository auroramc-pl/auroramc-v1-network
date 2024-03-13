package pl.auroramc.quests.objective;

import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.resource.Resource;
import pl.auroramc.quests.resource.key.ResourceKey;

public class Objective<T> extends Resource {

  private final T type;
  private final int saveInterval;
  private final ObjectiveGoalResolver goalResolver;
  private final List<ObjectiveRequirement> requirements;
  private MutableMessage message;

  protected Objective(
      final ResourceKey key,
      final T type,
      final int saveInterval,
      final ObjectiveGoalResolver goalResolver,
      final List<ObjectiveRequirement> requirements
  ) {
    super(key);
    this.type = type;
    this.saveInterval = saveInterval;
    this.goalResolver = goalResolver;
    this.requirements = requirements;
  }

  public T getType() {
    return type;
  }

  public int getSaveInterval() {
    return saveInterval;
  }

  public ObjectiveGoalResolver getGoalResolver() {
    return goalResolver;
  }

  public List<ObjectiveRequirement> getRequirements() {
    return requirements;
  }

  public MutableMessage getMessage() {
    return message;
  }

  @Internal
  public void setMessage(final MutableMessage message) {
    this.message = message;
  }
}
