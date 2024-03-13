package pl.auroramc.gamble.gamble;

public record GambleKey(String id) {

  public static GambleKey COINFLIP = new GambleKey("coinflip");
}
