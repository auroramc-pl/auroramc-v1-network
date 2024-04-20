package pl.auroramc.economy.account;

import java.util.logging.Logger;
import moe.rafal.juliet.Juliet;
import pl.auroramc.commons.scheduler.Scheduler;

public final class AccountFacadeFactory {

  private AccountFacadeFactory() {}

  public static AccountFacade getAccountFacade(final Scheduler scheduler, final Logger logger, final Juliet juliet) {
    final SqlAccountRepository sqlAccountRepository = new SqlAccountRepository(logger, juliet);
    sqlAccountRepository.createAccountSchemaIfRequired();
    return new AccountService(scheduler, sqlAccountRepository);
  }
}
