package pl.auroramc.gamble.gamble;

public abstract class Gamble {

  private final GambleContext gambleContext;

  protected Gamble(final GambleContext gambleContext) {
    this.gambleContext = gambleContext;
  }

  protected abstract Participant pickWinner();

  protected GambleContext getGambleContext() {
    return gambleContext;
  }
}
