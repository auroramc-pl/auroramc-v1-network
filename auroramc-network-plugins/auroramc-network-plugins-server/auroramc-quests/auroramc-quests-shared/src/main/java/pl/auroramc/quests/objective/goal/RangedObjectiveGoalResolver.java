package pl.auroramc.quests.objective.goal;

import java.util.concurrent.ThreadLocalRandom;
import pl.auroramc.quests.objective.ObjectiveGoalResolver;

public class RangedObjectiveGoalResolver implements ObjectiveGoalResolver {

  private final int minimumGoal;
  private final int maximumGoal;

  public RangedObjectiveGoalResolver(final int minimumGoal, final int maximumGoal) {
    this.minimumGoal = minimumGoal;
    this.maximumGoal = maximumGoal;
  }

  @Override
  public int resolveGoal() {
    return ThreadLocalRandom.current().nextInt(minimumGoal, maximumGoal + 1);
  }
}
