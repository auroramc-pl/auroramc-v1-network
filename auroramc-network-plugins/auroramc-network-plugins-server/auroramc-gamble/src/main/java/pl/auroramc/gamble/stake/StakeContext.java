package pl.auroramc.gamble.stake;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import pl.auroramc.gamble.gamble.GambleKey;
import pl.auroramc.gamble.gamble.Participant;

public record StakeContext(
    GambleKey gambleKey, UUID stakeUniqueId, BigDecimal stake, Participant initiator) {

  public static StakeContextBuilder newBuilder() {
    return new StakeContextBuilder();
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    final StakeContext that = (StakeContext) object;
    return
        Objects.equals(gambleKey, that.gambleKey) &&
        Objects.equals(stakeUniqueId, that.stakeUniqueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gambleKey, stakeUniqueId);
  }
}
