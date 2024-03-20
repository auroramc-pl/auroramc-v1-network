package pl.auroramc.quests.objective.requirement;

import java.util.ArrayList;
import java.util.List;

public class ObjectiveRequirementDsl {

  private final List<ObjectiveRequirement> requirements;

  public ObjectiveRequirementDsl() {
    this.requirements = new ArrayList<>();
  }

  public void requirement(final ObjectiveRequirement requirement) {
    requirements.add(requirement);
  }

  public List<ObjectiveRequirement> requirements() {
    return requirements;
  }
}
