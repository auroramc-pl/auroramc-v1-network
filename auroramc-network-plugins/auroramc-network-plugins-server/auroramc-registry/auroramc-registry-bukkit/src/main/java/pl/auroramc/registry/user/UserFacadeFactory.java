package pl.auroramc.registry.user;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;

public final class UserFacadeFactory {

  private UserFacadeFactory() {}

  public static UserFacade getUserFacade(final Logger logger, final Juliet juliet) {
    final SqlUserRepository sqlUserRepository = new SqlUserRepository(juliet);
    sqlUserRepository.createUserSchemaIfRequired();
    return new UserService(logger, sqlUserRepository);
  }
}
