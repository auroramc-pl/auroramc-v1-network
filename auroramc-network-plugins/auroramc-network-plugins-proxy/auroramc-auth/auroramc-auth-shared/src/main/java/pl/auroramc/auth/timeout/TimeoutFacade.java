package pl.auroramc.auth.timeout;

import java.time.Duration;
import java.util.UUID;

public interface TimeoutFacade {

  Duration getRemainingPeriod(final UUID uniqueId);

  void startCountdown(final UUID uniqueId);

  void ditchCountdown(final UUID uniqueId);

  boolean hasCountdown(final UUID uniqueId);
}
