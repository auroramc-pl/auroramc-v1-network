package pl.auroramc.dailyrewards.visit;

import java.time.Duration;
import java.time.Instant;

public class Visit {

  private Long userId;
  private Duration duration;
  private Instant startTime;
  private Instant ditchTime;

  public Visit(
      final Long userId,
      final Duration duration,
      final Instant startTime,
      final Instant ditchTime) {
    this.userId = userId;
    this.duration = duration;
    this.startTime = startTime;
    this.ditchTime = ditchTime;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(final Duration duration) {
    this.duration = duration;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public void setStartTime(final Instant startTime) {
    this.startTime = startTime;
  }

  public Instant getDitchTime() {
    return ditchTime;
  }

  public void setDitchTime(final Instant ditchTime) {
    this.ditchTime = ditchTime;
  }
}
