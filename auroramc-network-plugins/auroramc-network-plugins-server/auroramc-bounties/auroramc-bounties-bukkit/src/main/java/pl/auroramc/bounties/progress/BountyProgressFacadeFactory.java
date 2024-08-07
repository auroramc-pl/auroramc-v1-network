package pl.auroramc.bounties.progress;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class BountyProgressFacadeFactory {

  private BountyProgressFacadeFactory() {}

  public static BountyProgressFacade getBountyProgressFacade(
      final Scheduler scheduler, final Juliet juliet) {
    final SqlBountyProgressRepository bountyProgressRepository =
        new SqlBountyProgressRepository(juliet);
    bountyProgressRepository.createBountyProgressSchemaIfRequired();
    return new BountyProgressService(scheduler, bountyProgressRepository);
  }
}
