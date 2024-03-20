package pl.auroramc.dailyrewards.visit;

import java.time.Duration;
import java.time.Instant;

public class Visit {

  private Long userId;
  private Duration sessionDuration;
  private Instant sessionStartTime;
  private Instant sessionDitchTime;

  public Visit(
      final Long userId,
      final Duration sessionDuration,
      final Instant sessionStartTime,
      final Instant sessionDitchTime) {
    this.userId = userId;
    this.sessionDuration = sessionDuration;
    this.sessionStartTime = sessionStartTime;
    this.sessionDitchTime = sessionDitchTime;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public Duration getSessionDuration() {
    return sessionDuration;
  }

  public void setSessionDuration(final Duration sessionDuration) {
    this.sessionDuration = sessionDuration;
  }

  public Instant getSessionStartTime() {
    return sessionStartTime;
  }

  public void setSessionStartTime(final Instant sessionStartTime) {
    this.sessionStartTime = sessionStartTime;
  }

  public Instant getSessionDitchTime() {
    return sessionDitchTime;
  }

  public void setSessionDitchTime(final Instant sessionDitchTime) {
    this.sessionDitchTime = sessionDitchTime;
  }
}
