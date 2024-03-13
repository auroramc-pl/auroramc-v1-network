package pl.auroramc.gamble.stake;

import java.math.BigDecimal;
import java.util.UUID;
import pl.auroramc.gamble.gamble.GambleKey;
import pl.auroramc.gamble.gamble.Participant;

public final class StakeContextBuilder {

  private GambleKey gambleKey;
  private UUID stakeUniqueId;
  private BigDecimal stake;

  private Participant participant;

  StakeContextBuilder() {

  }

  public StakeContextBuilder gambleKey(final GambleKey gambleKey) {
    this.gambleKey = gambleKey;
    return this;
  }

  public StakeContextBuilder stakeUniqueId(final UUID stakeUniqueId) {
    this.stakeUniqueId = stakeUniqueId;
    return this;
  }

  public StakeContextBuilder stake(final BigDecimal stake) {
    this.stake = stake;
    return this;
  }

  public StakeContextBuilder participant(final Participant participant) {
    this.participant = participant;
    return this;
  }

  public StakeContext build() {
    return new StakeContext(gambleKey, stakeUniqueId, stake, participant);
  }
}
