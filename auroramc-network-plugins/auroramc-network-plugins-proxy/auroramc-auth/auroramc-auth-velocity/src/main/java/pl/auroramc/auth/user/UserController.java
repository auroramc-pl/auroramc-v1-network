package pl.auroramc.auth.user;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import pl.auroramc.auth.AuthConfig;
import pl.auroramc.auth.account.AccountFacade;

public class UserController {

  private final Logger logger;
  private final ProxyServer server;
  private final AuthConfig authConfig;
  private final UserFacade userFacade;
  private final AccountFacade accountFacade;

  public UserController(
      final Logger logger, final ProxyServer server, final AuthConfig authConfig, final UserFacade userFacade, final AccountFacade accountFacade) {
    this.logger = logger;
    this.server = server;
    this.authConfig = authConfig;
    this.userFacade = userFacade;
    this.accountFacade = accountFacade;
  }

  public CompletableFuture<Void> authenticateUser(final Player player) {
    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenCompose(user -> createUserIfNotExists(player, user))
        .thenAccept(user -> user.setAuthenticated(player.isOnlineMode()))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<User> createUserIfNotExists(
      final Player player, final User user) {
    if (user == null) {
      return createUser(player);
    }

    return completedFuture(user);
  }

  private CompletableFuture<User> createUser(final Player player) {
    return accountFacade.getPremiumUniqueIdByUsername(player.getUsername())
        .thenApply(premiumUniqueId ->
            new User(null, player.getUniqueId(), player.getUsername(), null, null, premiumUniqueId, player.isOnlineMode()))
        .thenCompose(userFacade::createUser)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private <T> void redirectUser(
      final T source,
      final User user,
      final RegisteredServer originalServer,
      final RegisteredServer previousServer,
      final BiConsumer<T, RegisteredServer> redirectingFunction) {
    if (user.isAuthenticated() && previousServer != null) {
      return;
    }

    final RegisteredServer destinedServer = getDestinedServer(user);
    if (destinedServer.equals(originalServer)) {
      return;
    }

    redirectingFunction.accept(source, destinedServer);
  }

  public void redirectUser(final PlayerChooseInitialServerEvent event, final User user) {
    event.setInitialServer(getDestinedServer(user));
  }

  public void redirectUser(final Player player, final User user) {
    player.getCurrentServer().ifPresent(connection ->
        redirectUser(player, user,
            connection.getServer(),
            connection.getPreviousServer().orElse(null),
            (source, destinedServer) -> player.createConnectionRequest(destinedServer).fireAndForget()
        )
    );
  }

  public RegisteredServer getDestinedServer(final User user) {
    return server.getServer(
        user.isAuthenticated()
            ? authConfig.destinedServerId
            : authConfig.awaitingServerId
        )
        .orElseThrow(() ->
            new UserRedirectingException(
                "Could not resolve destined server by id specified in configuration, verify whether specified server exists."
            )
        );
  }
}
