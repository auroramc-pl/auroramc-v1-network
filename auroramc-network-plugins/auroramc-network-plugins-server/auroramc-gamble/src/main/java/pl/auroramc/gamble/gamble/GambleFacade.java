package pl.auroramc.gamble.gamble;

import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public interface GambleFacade {

  static GambleFacade getGambleFacade(
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EconomyFacade economyFacade) {
    return new GambleService(fundsCurrency, messageSource, messageCompiler, economyFacade);
  }

  void settleGamble(final Gamble gamble);
}
