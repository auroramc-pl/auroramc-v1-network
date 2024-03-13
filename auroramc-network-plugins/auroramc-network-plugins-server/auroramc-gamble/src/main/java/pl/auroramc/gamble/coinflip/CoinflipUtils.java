package pl.auroramc.gamble.coinflip;

import java.util.concurrent.ThreadLocalRandom;

final class CoinflipUtils {

  private CoinflipUtils() {

  }

  static CoinSide flipCoin() {
    return CoinSide.values()[ThreadLocalRandom.current().nextInt(CoinSide.values().length)];
  }
}
