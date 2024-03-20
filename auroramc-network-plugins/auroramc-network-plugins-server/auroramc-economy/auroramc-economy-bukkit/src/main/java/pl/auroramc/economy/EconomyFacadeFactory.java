package pl.auroramc.economy;

import java.util.logging.Logger;
import pl.auroramc.economy.account.AccountFacade;
import pl.auroramc.economy.payment.PaymentFacade;
import pl.auroramc.registry.user.UserFacade;

public final class EconomyFacadeFactory {

  private EconomyFacadeFactory() {}

  public static EconomyFacade getEconomyFacade(
      final Logger logger,
      final UserFacade userFacade,
      final AccountFacade accountFacade,
      final PaymentFacade paymentFacade) {
    return new EconomyService(logger, userFacade, accountFacade, paymentFacade);
  }
}
