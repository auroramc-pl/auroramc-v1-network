package pl.auroramc.registry.user;

import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class UserFacadeFactory {

  private UserFacadeFactory() {}

  public static UserFacade getUserFacade(final Scheduler scheduler, final Juliet juliet) {
    final SqlUserRepository sqlUserRepository = new SqlUserRepository(juliet);
    sqlUserRepository.createUserSchemaIfRequired();
    return new UserService(scheduler, sqlUserRepository);
  }
}
