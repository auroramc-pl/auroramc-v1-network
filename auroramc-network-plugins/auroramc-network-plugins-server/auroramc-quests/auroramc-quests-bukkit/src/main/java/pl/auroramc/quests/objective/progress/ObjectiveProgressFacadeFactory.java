package pl.auroramc.quests.objective.progress;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class ObjectiveProgressFacadeFactory {

  private ObjectiveProgressFacadeFactory() {}

  public static ObjectiveProgressFacade getObjectiveProgressFacade(
      final Scheduler scheduler, final Juliet juliet) {
    final SqlObjectiveProgressRepository sqlObjectiveProgressRepository =
        new SqlObjectiveProgressRepository(juliet);
    sqlObjectiveProgressRepository.createObjectiveProgressSchemaIfRequired();
    return new ObjectiveProgressService(scheduler, sqlObjectiveProgressRepository);
  }
}
