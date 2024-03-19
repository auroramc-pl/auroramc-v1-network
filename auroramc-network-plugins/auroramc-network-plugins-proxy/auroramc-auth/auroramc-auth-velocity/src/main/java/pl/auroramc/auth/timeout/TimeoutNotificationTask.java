package pl.auroramc.auth.timeout;

import static java.time.Duration.ZERO;
import static pl.auroramc.auth.message.MutableMessageVariableKey.PERIOD_VARIABLE_KEY;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.time.Duration;
import pl.auroramc.auth.message.MutableMessageSource;
import pl.auroramc.commons.duration.DurationFormatter;

public class TimeoutNotificationTask implements Runnable {

  private final ProxyServer server;
  private final MutableMessageSource messageSource;
  private final TimeoutFacade timeoutFacade;
  private final DurationFormatter durationFormatter;

  public TimeoutNotificationTask(
      final ProxyServer server,
      final MutableMessageSource messageSource,
      final TimeoutFacade timeoutFacade,
      final DurationFormatter durationFormatter
  ) {
    this.server = server;
    this.messageSource = messageSource;
    this.timeoutFacade = timeoutFacade;
    this.durationFormatter = durationFormatter;
  }

  @Override
  public void run() {
    for (final Player player : server.getAllPlayers()) {
      if (timeoutFacade.hasCountdown(player.getUniqueId())) {
        processTimeoutSequence(player);
      }
    }
  }

  private void processTimeoutSequence(final Player player) {
    final Duration remainingPeriod = timeoutFacade.getRemainingPeriod(player.getUniqueId());
    if (remainingPeriod.compareTo(ZERO) >= 0) {
      player.sendActionBar(
          messageSource.authorizationTicking
              .with(PERIOD_VARIABLE_KEY, durationFormatter.getFormattedDuration(remainingPeriod))
              .compile()
      );
      return;
    }

    timeoutFacade.ditchCountdown(player.getUniqueId());
    player.disconnect(messageSource.authorizationTimeout.compile());
  }
}
