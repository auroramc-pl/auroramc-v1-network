package pl.auroramc.dailyrewards.visit;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class VisitFacadeFactory {

  private VisitFacadeFactory() {

  }

  public static VisitFacade getVisitFacade(final Logger logger, final Juliet juliet) {
    final SqlVisitRepository sqlVisitRepository = new SqlVisitRepository(juliet);
    sqlVisitRepository.createVisitSchemaIfRequired();
    return new VisitService(logger, sqlVisitRepository);
  }
}
