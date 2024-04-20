package pl.auroramc.gamble.coinflip;

import static pl.auroramc.gamble.coinflip.CoinflipUtils.flipCoin;

import pl.auroramc.gamble.gamble.Gamble;
import pl.auroramc.gamble.gamble.context.GambleContext;
import pl.auroramc.gamble.participant.Participant;

public class CoinflipGamble extends Gamble {

  public CoinflipGamble(final GambleContext gambleContext) {
    super(gambleContext);
  }

  @Override
  public Participant pickWinner() {
    return getGambleContext().getParticipantByPrediction(flipCoin());
  }
}
