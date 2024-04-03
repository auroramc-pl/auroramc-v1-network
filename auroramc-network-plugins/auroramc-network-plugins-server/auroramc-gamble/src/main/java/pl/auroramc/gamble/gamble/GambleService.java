package pl.auroramc.gamble.gamble;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.COMPETITOR_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.CURRENCY_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.STAKE_PATH;
import static pl.auroramc.gamble.message.MutableMessageVariableKey.UNIQUE_ID_PATH;

import java.util.logging.Logger;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MutableMessageSource;

class GambleService implements GambleFacade {

  private final Logger logger;
  private final Currency fundsCurrency;
  private final MutableMessageSource messageSource;
  private final EconomyFacade economyFacade;

  GambleService(
      final Logger logger,
      final Currency fundsCurrency,
      final MutableMessageSource messageSource,
      final EconomyFacade economyFacade
  ) {
    this.logger = logger;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
  }

  @Override
  public void settleGamble(final Gamble gamble) {
    final Participant winnerParticipant = gamble.pickWinner();
    final Participant losingParticipant = getOpponent(gamble, winnerParticipant);

    economyFacade.deposit(gamble.getGambleContext().initiator().uniqueId(), fundsCurrency, gamble.getGambleContext().stake())
        .thenCompose(state ->
            economyFacade.transfer(
                losingParticipant.uniqueId(),
                winnerParticipant.uniqueId(),
                fundsCurrency,
                gamble.getGambleContext().stake()
            )
        )
        .thenAccept(state -> {
          winnerParticipant.sendMessage(
              messageSource.stakeWon
                  .with(UNIQUE_ID_PATH, gamble.getGambleContext().gambleUniqueId().toString())
                  .with(CURRENCY_PATH, fundsCurrency.getSymbol())
                  .with(STAKE_PATH, getFormattedDecimal(gamble.getGambleContext().stake()))
                  .with(COMPETITOR_PATH, losingParticipant.username())
                  .compile()
          );
          losingParticipant.sendMessage(
              messageSource.stakeLost
                  .with(UNIQUE_ID_PATH, gamble.getGambleContext().gambleUniqueId().toString())
                  .with(CURRENCY_PATH, fundsCurrency.getSymbol())
                  .with(STAKE_PATH, getFormattedDecimal(gamble.getGambleContext().stake()))
                  .with(COMPETITOR_PATH, winnerParticipant.username())
                  .compile()
          );
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private Participant getOpponent(final Gamble gamble, final Participant participant) {
    return gamble.getGambleContext().initiator().equals(participant)
        ? gamble.getGambleContext().competitor()
        : gamble.getGambleContext().initiator();
  }
}
