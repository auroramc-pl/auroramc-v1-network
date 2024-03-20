package pl.auroramc.quests.objectives.travel;

import java.util.List;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveGoalResolver;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.resource.key.ResourceKey;

public class DistanceObjective extends Objective<Byte> {

  public DistanceObjective(
      final ResourceKey key,
      final Byte type,
      final int saveInterval,
      final ObjectiveGoalResolver goalResolver,
      final List<ObjectiveRequirement> requirements) {
    super(key, type, saveInterval, goalResolver, requirements);
  }
}
