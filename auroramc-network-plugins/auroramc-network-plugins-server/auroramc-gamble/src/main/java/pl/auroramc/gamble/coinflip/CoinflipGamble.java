package pl.auroramc.gamble.coinflip;

import static pl.auroramc.gamble.coinflip.CoinflipUtils.flipCoin;

import pl.auroramc.gamble.gamble.Gamble;
import pl.auroramc.gamble.gamble.GambleContext;
import pl.auroramc.gamble.gamble.Participant;

public class CoinflipGamble extends Gamble {

  public CoinflipGamble(final GambleContext gambleContext) {
    super(gambleContext);
  }

  @Override
  public Participant pickWinner() {
    return getGambleContext().getParticipantByPrediction(flipCoin());
  }
}
