package pl.auroramc.registry.user;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class UserListener implements Listener {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private final Logger logger;
  private final UserFacade userFacade;

  public UserListener(final Logger logger, final UserFacade userFacade) {
    this.logger = logger;
    this.userFacade = userFacade;
  }

  @EventHandler
  public void onUserValidation(final AsyncPlayerPreLoginEvent event) {
    userFacade
        .getUserByUniqueId(event.getUniqueId())
        .thenCompose(user -> validateUser(user, event.getUniqueId(), event.getName()))
        .exceptionally(exception -> delegateCaughtException(logger, exception))
        .join();
  }

  private CompletableFuture<Void> validateUser(
      final User user, final UUID uniqueId, final String username) {
    if (user == null) {
      return userFacade.createUser(new User(null, uniqueId, username));
    }

    if (username.equals(user.getUsername())) {
      return EMPTY_FUTURE;
    }

    user.setUsername(username);
    return userFacade.updateUser(user);
  }
}
