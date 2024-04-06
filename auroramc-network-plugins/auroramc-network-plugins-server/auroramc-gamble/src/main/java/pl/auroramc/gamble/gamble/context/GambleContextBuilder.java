package pl.auroramc.gamble.gamble.context;

import java.math.BigDecimal;
import java.util.UUID;
import pl.auroramc.gamble.participant.Participant;

public class GambleContextBuilder {

  private UUID gambleUniqueId;
  private BigDecimal stake;
  private Participant initiator;
  private Participant competitor;

  GambleContextBuilder() {}

  public GambleContextBuilder gambleUniqueId(final UUID gambleUniqueId) {
    this.gambleUniqueId = gambleUniqueId;
    return this;
  }

  public GambleContextBuilder stake(final BigDecimal stake) {
    this.stake = stake;
    return this;
  }

  public GambleContextBuilder initiator(final Participant initiator) {
    this.initiator = initiator;
    return this;
  }

  public GambleContextBuilder competitor(final Participant competitor) {
    this.competitor = competitor;
    return this;
  }

  public GambleContext build() {
    return new GambleContext(gambleUniqueId, stake, initiator, competitor);
  }
}
