package pl.auroramc.quests.objective.goal;

import pl.auroramc.quests.objective.ObjectiveGoalResolver;

public class DefinedObjectiveGoalResolver implements ObjectiveGoalResolver {

  private final int definedGoal;

  public DefinedObjectiveGoalResolver(final int definedGoal) {
    this.definedGoal = definedGoal;
  }

  @Override
  public int resolveGoal() {
    return definedGoal;
  }
}
