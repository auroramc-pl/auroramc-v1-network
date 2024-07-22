package pl.auroramc.quests.objectives.block;

import java.util.List;
import org.bukkit.Material;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveGoalResolver;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.registry.resource.key.ResourceKey;

public class BreakBlockObjective extends Objective<Material> {

  public BreakBlockObjective(
      final ResourceKey key,
      final Material type,
      final int saveInterval,
      final ObjectiveGoalResolver goalResolver,
      final List<ObjectiveRequirement> requirements) {
    super(key, type, saveInterval, goalResolver, requirements);
  }
}
