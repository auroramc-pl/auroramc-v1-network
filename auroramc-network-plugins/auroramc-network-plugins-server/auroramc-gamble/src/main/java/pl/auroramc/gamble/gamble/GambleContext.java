package pl.auroramc.gamble.gamble;

import java.math.BigDecimal;
import java.util.UUID;

public record GambleContext(
    UUID gambleUniqueId, BigDecimal stake, Participant initiator, Participant competitor
) {

  public static GambleContextBuilder newBuilder() {
    return new GambleContextBuilder();
  }

  public Participant getParticipantByPrediction(final Object prediction) {
    if (initiator.prediction().equals(prediction)) {
      return initiator;
    } else if (competitor.prediction().equals(prediction)) {
      return competitor;
    } else {
      throw new ParticipantResolvingException(
          "Could not resolve initiator by prediction."
      );
    }
  }
}
