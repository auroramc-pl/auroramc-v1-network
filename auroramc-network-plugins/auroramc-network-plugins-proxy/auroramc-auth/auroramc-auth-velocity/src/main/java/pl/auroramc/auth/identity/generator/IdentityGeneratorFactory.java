package pl.auroramc.auth.identity.generator;

import pl.auroramc.auth.account.AccountFacade;
import pl.auroramc.auth.identity.IdentityGenerator;

public final class IdentityGeneratorFactory {

  private IdentityGeneratorFactory() {

  }

  public static IdentityGenerator getIdentityGenerator(final AccountFacade accountFacade) {
    return new MojangIdentityGenerator(accountFacade);
  }
}
