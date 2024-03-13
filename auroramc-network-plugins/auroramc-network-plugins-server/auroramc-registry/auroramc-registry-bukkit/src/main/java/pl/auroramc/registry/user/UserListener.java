package pl.auroramc.registry.user;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.bukkit.event.EventPriority.LOWEST;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserListener implements Listener {

  private static final CompletableFuture<Void> EMPTY_FUTURE = completedFuture(null);
  private final Logger logger;
  private final UserFacade userFacade;

  public UserListener(final Logger logger, final UserFacade userFacade) {
    this.logger = logger;
    this.userFacade = userFacade;
  }

  @EventHandler(priority = LOWEST)
  public void onUserValidation(final PlayerJoinEvent event) {
    userFacade.getUserByUniqueId(event.getPlayer().getUniqueId())
        .thenCompose(user -> validateUser(user, event.getPlayer()))
        .exceptionally(exception -> delegateCaughtException(logger, exception))
        .join();
  }

  private CompletableFuture<Void> validateUser(final User user, final Player player) {
    if (user == null) {
      return userFacade.createUser(new User(null, player.getUniqueId(), player.getName()));
    }

    final String currentUsername = player.getName();
    final String persistUsername = user.getUsername();
    if (currentUsername.equals(persistUsername)) {
      return EMPTY_FUTURE;
    }

    user.setUsername(currentUsername);
    return userFacade.updateUser(user);
  }
}
