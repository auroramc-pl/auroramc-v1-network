package pl.auroramc.auth.timeout;

import static java.time.Duration.ZERO;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.time.Duration;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.commons.duration.DurationFormatter;

public class TimeoutNotifyingTask implements Runnable {

  private final ProxyServer server;
  private final MessageSource messageSource;
  private final TimeoutFacade timeoutFacade;
  private final DurationFormatter durationFormatter;

  public TimeoutNotifyingTask(
      final ProxyServer server,
      final MessageSource messageSource,
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
        tickTimeout(player);
      }
    }
  }

  private void tickTimeout(final Player player) {
    final Duration remainingPeriod = timeoutFacade.getRemainingPeriod(player.getUniqueId());
    if (remainingPeriod.compareTo(ZERO) >= 0) {
      player.sendActionBar(
          messageSource.authorizationTicking
              .with("period", durationFormatter.getFormattedDuration(remainingPeriod))
              .compile()
      );
      return;
    }

    timeoutFacade.ditchCountdown(player.getUniqueId());
    player.disconnect(messageSource.authorizationTimeout.compile());
  }
}
