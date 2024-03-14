package pl.auroramc.auth.account;

import static com.velocitypowered.api.event.EventTask.resumeWhenComplete;
import static com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult.forceOfflineMode;
import static com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult.forceOnlineMode;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;

public class AccountListener {

  private final Logger logger;
  private final UserFacade userFacade;
  private final AccountFacade accountFacade;

  public AccountListener(
      final Logger logger,
      final UserFacade userFacade,
      final AccountFacade accountFacade
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.accountFacade = accountFacade;
  }

  @Subscribe
  public void onAccountConnectionModeRequest(
      final PreLoginEvent event, final Continuation continuation
  ) {
    resumeWhenComplete(
        userFacade.getUserByUsername(event.getUsername())
            .thenCompose(user ->
                verifyWhetherUserIsPremiumOrRetrieveByUsername(user, event.getUsername())
            )
            .thenApply(this::translateConnectionMode)
            .thenAccept(event::setResult)
            .exceptionally(exception -> delegateCaughtException(logger, exception))
    ).execute(continuation);
  }

  private CompletableFuture<Boolean> verifyWhetherUserIsPremiumOrRetrieveByUsername(
      final User user, final String username
  ) {
    if (user == null) {
      return accountFacade.getPremiumUniqueIdByUsername(username)
          .thenApply(this::whetherPremiumUniqueIdIsNotNull);
    }

    return completedFuture(user.isPremium());
  }

  private boolean whetherPremiumUniqueIdIsNotNull(final UUID premiumUniqueId) {
    return premiumUniqueId != null;
  }

  private PreLoginComponentResult translateConnectionMode(
      final boolean requestWhetherUsernameIsPremium
  ) {
    return requestWhetherUsernameIsPremium
        ? forceOnlineMode()
        : forceOfflineMode();
  }
}
