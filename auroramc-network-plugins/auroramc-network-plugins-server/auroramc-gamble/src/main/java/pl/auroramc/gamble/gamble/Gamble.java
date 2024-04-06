package pl.auroramc.gamble.gamble;

import pl.auroramc.gamble.gamble.context.GambleContext;
import pl.auroramc.gamble.participant.Participant;

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
