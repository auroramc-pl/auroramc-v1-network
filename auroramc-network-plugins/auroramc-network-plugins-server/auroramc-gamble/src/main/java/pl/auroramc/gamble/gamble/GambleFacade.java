package pl.auroramc.gamble.gamble;

import java.util.logging.Logger;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

public interface GambleFacade {

  static GambleFacade getGambleFacade(
      final Logger logger, final Currency fundsCurrency, final EconomyFacade economyFacade
  ) {
    return new GambleService(logger, fundsCurrency, economyFacade);
  }

  void settleGamble(final Gamble gamble);
}
