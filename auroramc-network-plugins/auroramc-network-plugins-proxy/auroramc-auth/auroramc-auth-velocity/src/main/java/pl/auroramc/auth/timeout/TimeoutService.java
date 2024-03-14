package pl.auroramc.auth.timeout;

import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class TimeoutService implements TimeoutFacade {

  private static final Duration TIMEOUT_DURATION = ofSeconds(45);
  private final Map<UUID, Instant> timeoutMap;

  TimeoutService() {
    this.timeoutMap = new HashMap<>();
  }

  @Override
  public Duration getRemainingPeriod(final UUID uniqueId) {
    return Optional.ofNullable(timeoutMap.get(uniqueId))
        .map(expirationTime -> between(now(), expirationTime))
        .orElse(null);
  }

  @Override
  public void startCountdown(final UUID uniqueId) {
    timeoutMap.put(uniqueId, now().plus(TIMEOUT_DURATION));
  }

  @Override
  public void ditchCountdown(final UUID uniqueId) {
    timeoutMap.remove(uniqueId);
  }

  @Override
  public boolean hasCountdown(final UUID uniqueId) {
    return timeoutMap.containsKey(uniqueId);
  }
}
