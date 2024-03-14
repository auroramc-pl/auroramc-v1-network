package pl.auroramc.auth.timeout;

import static com.velocitypowered.api.event.EventTask.resumeWhenComplete;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;

public class TimeoutListener {

  private final UserFacade userFacade;
  private final TimeoutFacade timeoutFacade;

  public TimeoutListener(final UserFacade userFacade, final TimeoutFacade timeoutFacade) {
    this.userFacade = userFacade;
    this.timeoutFacade = timeoutFacade;
  }

  @Subscribe
  public void onTimeoutStart(final ServerConnectedEvent event, final Continuation continuation) {
    resumeWhenComplete(
        userFacade.getUserByUniqueId(event.getPlayer().getUniqueId())
            .thenAccept(this::startTimeoutIfRequired)
    ).execute(continuation);
  }

  private void startTimeoutIfRequired(final User user) {
    if (user.isAuthenticated()) {
      return;
    }

    timeoutFacade.startCountdown(user.getUniqueId());
  }

  @Subscribe
  public void onTimeoutDitch(final DisconnectEvent event) {
    timeoutFacade.ditchCountdown(event.getPlayer().getUniqueId());
  }
}
