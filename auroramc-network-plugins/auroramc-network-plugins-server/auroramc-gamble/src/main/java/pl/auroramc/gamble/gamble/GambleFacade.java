package pl.auroramc.gamble.gamble;

import java.util.logging.Logger;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MessageSource;

public interface GambleFacade {

  static GambleFacade getGambleFacade(
      final Logger logger,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final EconomyFacade economyFacade
  ) {
    return new GambleService(
        logger, fundsCurrency, messageSource, economyFacade
    );
  }

  void settleGamble(final Gamble gamble);
}
