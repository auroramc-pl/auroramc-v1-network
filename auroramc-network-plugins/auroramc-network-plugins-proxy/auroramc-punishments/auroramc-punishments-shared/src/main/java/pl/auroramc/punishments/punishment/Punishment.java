package pl.auroramc.punishments.punishment;

import java.time.Duration;
import java.time.Instant;

public class Punishment {

  private Long id;
  private Long penalizedId;
  private Long performerId;
  private String reason;
  private Duration period;
  private PunishmentScope scope;
  private PunishmentState state;
  private Instant issuedAt;
  private Instant expiresAt;

  public Punishment(
      final Long id,
      final Long penalizedId,
      final Long performerId,
      final String reason,
      final Duration period,
      final PunishmentScope scope,
      final PunishmentState state,
      final Instant issuedAt,
      final Instant expiresAt) {
    this.id = id;
    this.penalizedId = penalizedId;
    this.performerId = performerId;
    this.reason = reason;
    this.period = period;
    this.scope = scope;
    this.state = state;
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getPenalizedId() {
    return penalizedId;
  }

  public void setPenalizedId(final Long penalizedId) {
    this.penalizedId = penalizedId;
  }

  public Long getPerformerId() {
    return performerId;
  }

  public void setPerformerId(final Long performerId) {
    this.performerId = performerId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public Duration getPeriod() {
    return period;
  }

  public void setPeriod(final Duration period) {
    this.period = period;
  }

  public PunishmentScope getScope() {
    return scope;
  }

  public void setScope(final PunishmentScope scope) {
    this.scope = scope;
  }

  public PunishmentState getState() {
    return state;
  }

  public void setState(final PunishmentState state) {
    this.state = state;
  }

  public Instant getIssuedAt() {
    return issuedAt;
  }

  public void setIssuedAt(final Instant issuedAt) {
    this.issuedAt = issuedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(final Instant expiresAt) {
    this.expiresAt = expiresAt;
  }
}
