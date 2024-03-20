package pl.auroramc.auth.account;

import static com.velocitypowered.api.event.EventTask.resumeWhenComplete;
import static com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult.forceOfflineMode;
import static com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult.forceOnlineMode;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;

public class AccountListener {

  private final Logger logger;
  private final UserFacade userFacade;
  private final AccountFacade accountFacade;

  public AccountListener(
      final Logger logger, final UserFacade userFacade, final AccountFacade accountFacade) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.accountFacade = accountFacade;
  }

  @Subscribe
  public void onAccountConnectionModeRequest(
      final PreLoginEvent event, final Continuation continuation) {
    resumeWhenComplete(
            userFacade
                .getUserByUsername(event.getUsername())
                .thenCompose(user -> hasPremium(user, event.getUsername()))
                .thenApply(
                    requiresAuthentication ->
                        requiresAuthentication ? forceOnlineMode() : forceOfflineMode())
                .thenAccept(event::setResult)
                .exceptionally(exception -> delegateCaughtException(logger, exception)))
        .execute(continuation);
  }

  private CompletableFuture<Boolean> hasPremium(final User user, final String username) {
    if (user == null) {
      return accountFacade.getPremiumUniqueIdByUsername(username).thenApply(Objects::nonNull);
    }

    return completedFuture(user.isPremium());
  }
}
