package pl.auroramc.gamble.coinflip;

public enum CoinSide {

  HEADS,
  TAILS;

  public CoinSide opposite() {
    return this == HEADS ? TAILS : HEADS;
  }
}
