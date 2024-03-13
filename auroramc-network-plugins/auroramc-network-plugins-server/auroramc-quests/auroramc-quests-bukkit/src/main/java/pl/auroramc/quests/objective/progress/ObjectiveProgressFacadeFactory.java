package pl.auroramc.quests.objective.progress;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class ObjectiveProgressFacadeFactory {

  private ObjectiveProgressFacadeFactory() {

  }

  public static ObjectiveProgressFacade getObjectiveProgressFacade(
      final Logger logger, final Juliet juliet
  ) {
    final SqlObjectiveProgressRepository sqlObjectiveProgressRepository = new SqlObjectiveProgressRepository(juliet);
    sqlObjectiveProgressRepository.createObjectiveProgressSchemaIfRequired();
    return new ObjectiveProgressService(logger, sqlObjectiveProgressRepository);
  }
}
