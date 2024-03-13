package pl.auroramc.gamble.gamble;

import static pl.auroramc.gamble.gamble.GambleKey.COINFLIP;

import pl.auroramc.gamble.coinflip.CoinflipGamble;
import pl.auroramc.gamble.stake.StakeContext;

public final class GambleFactory {

  private GambleFactory() {

  }

  public static Gamble getGamble(
      final GambleContext gambleContext, final StakeContext stakeContext
  ) {
    final GambleKey gambleKey = stakeContext.gambleKey();
    if (gambleKey.equals(COINFLIP)) {
      return new CoinflipGamble(gambleContext);
    }

    throw new GambleRetrievalException(
        "Could not produce gamble, because of unknown gamble key: (%s)"
            .formatted(
                gambleKey.id()
            )
    );
  }
}
