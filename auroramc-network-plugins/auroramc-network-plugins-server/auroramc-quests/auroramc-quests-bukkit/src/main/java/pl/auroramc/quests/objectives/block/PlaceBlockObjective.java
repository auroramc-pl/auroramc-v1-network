package pl.auroramc.quests.objectives.block;

import java.util.List;
import org.bukkit.Material;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveGoalResolver;
import pl.auroramc.quests.objective.requirement.ObjectiveRequirement;
import pl.auroramc.quests.resource.key.ResourceKey;

public class PlaceBlockObjective extends Objective<Material> {

  public PlaceBlockObjective(
      final ResourceKey key,
      final Material type,
      final int saveInterval,
      final ObjectiveGoalResolver goalResolver,
      final List<ObjectiveRequirement> requirements
  ) {
    super(key, type, saveInterval, goalResolver, requirements);
  }
}
