package pl.auroramc.registry.user;

import static pl.auroramc.commons.concurrent.CompletableFutureUtils.NIL;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;

public class UserListener implements Listener {

  private final UserFacade userFacade;

  public UserListener(final UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  @EventHandler
  public void onUserValidation(final AsyncPlayerPreLoginEvent event) {
    userFacade
        .getUserByUniqueId(event.getUniqueId())
        .thenApply(user -> new ValidationContext(user, event.getUniqueId(), event.getName()))
        .thenCompose(this::validateUser)
        .exceptionally(CompletableFutureUtils::delegateCaughtException)
        .join();
  }

  private CompletableFuture<Void> validateUser(final ValidationContext context) {
    final User user = context.user();
    if (user == null) {
      return userFacade.createUser(new User(null, context.uniqueId(), context.username()));
    }

    if (context.username().equals(user.getUsername())) {
      return NIL;
    }

    user.setUsername(context.username());
    return userFacade.updateUser(user);
  }

  private record ValidationContext(User user, UUID uniqueId, String username) {}
}
