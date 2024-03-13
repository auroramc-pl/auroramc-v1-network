package pl.auroramc.gamble.gamble;

import java.util.UUID;

public final class ParticipantBuilder {

  private UUID uniqueId;
  private String username;
  private Object prediction;

  ParticipantBuilder() {

  }

  public ParticipantBuilder uniqueId(final UUID uniqueId) {
    this.uniqueId = uniqueId;
    return this;
  }

  public ParticipantBuilder username(final String username) {
    this.username = username;
    return this;
  }

  public ParticipantBuilder prediction(final Object prediction) {
    this.prediction = prediction;
    return this;
  }

  public Participant build() {
    return new Participant(uniqueId, username, prediction);
  }
}
