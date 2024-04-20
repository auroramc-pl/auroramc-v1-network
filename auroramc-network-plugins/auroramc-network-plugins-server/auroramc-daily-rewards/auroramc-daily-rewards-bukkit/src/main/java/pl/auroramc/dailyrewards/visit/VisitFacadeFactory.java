package pl.auroramc.dailyrewards.visit;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class VisitFacadeFactory {

  private VisitFacadeFactory() {}

  public static VisitFacade getVisitFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlVisitRepository sqlVisitRepository = new SqlVisitRepository(juliet);
    sqlVisitRepository.createVisitSchemaIfRequired();
    return new VisitService(scheduler, sqlVisitRepository);
  }
}
