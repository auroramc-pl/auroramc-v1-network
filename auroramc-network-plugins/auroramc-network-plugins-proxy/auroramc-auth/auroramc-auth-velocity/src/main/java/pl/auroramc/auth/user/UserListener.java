package pl.auroramc.auth.user;

import static com.velocitypowered.api.event.EventTask.resumeWhenComplete;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import pl.auroramc.auth.identity.IdentityGenerator;
import pl.auroramc.auth.message.MutableMessageSource;

public class UserListener {

  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final UserFacade userFacade;
  private final UserController userController;
  private final IdentityGenerator identityGenerator;
  private final Pattern usernamePattern;

  public UserListener(
      final Logger logger,
      final MutableMessageSource messageSource,
      final UserFacade userFacade,
      final UserController userController,
      final IdentityGenerator identityGenerator,
      final String unparsedUsernamePattern
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.userController = userController;
    this.identityGenerator = identityGenerator;
    this.usernamePattern = Pattern.compile(unparsedUsernamePattern);
  }

  @Subscribe
  public void onUsernameVerification(final PreLoginEvent event) {
    if (usernamePattern.matcher(event.getUsername()).matches()) {
      return;
    }

    event.setResult(PreLoginComponentResult.denied(messageSource.specifiedUsernameIsInvalid.compile()));
  }

  @Subscribe
  public void onUserAuthentication(
      final PostLoginEvent event, final Continuation continuation) {
    resumeWhenComplete(
        userController.authenticateUser(event.getPlayer())
            .thenCompose(state -> userFacade.getUserByUniqueId(event.getPlayer().getUniqueId()))
            .thenApply(this::markUserAsAuthenticated)
            .thenApply(this::infoAboutAuthentication)
            .thenAccept(event.getPlayer()::sendMessage)
            .exceptionally(exception -> delegateCaughtException(logger, exception))
    ).execute(continuation);
  }

  private User markUserAsAuthenticated(final User user) {
    user.setAuthenticated(user.isPremium());
    return user;
  }

  private Component infoAboutAuthentication(final User user) {
    if (user.isAuthenticated()) {
      return messageSource.authorizedWithPremium.compile();
    }

    return (
        user.isRegistered()
            ? messageSource.suggestAuthorization
            : messageSource.suggestRegistration
    ).compile();
  }

  @Subscribe
  public void onUserServerConnection(
      final PlayerChooseInitialServerEvent event, final Continuation continuation
  ) {
    resumeWhenComplete(
        userFacade.getUserByUniqueId(event.getPlayer().getUniqueId())
            .thenAccept(user -> userController.redirectUser(event, user))
            .exceptionally(exception -> delegateCaughtException(logger, exception))
    ).execute(continuation);
  }

  @Subscribe
  public void onUserDisconnection(final DisconnectEvent event) {
    userFacade.deleteUserOfCacheByUniqueId(event.getPlayer().getUniqueId());
  }

  @Subscribe
  public void onUserGameProfileRequest(
      final GameProfileRequestEvent event, final Continuation continuation
  ) {
    resumeWhenComplete(
        userFacade.getUserByUsername(event.getUsername())
            .thenCompose(user -> getUniqueIdOrGenerate(user, event.getUsername()))
            .thenAccept(uniqueId -> event.setGameProfile(event.getGameProfile().withId(uniqueId)))
            .exceptionally(exception -> delegateCaughtException(logger, exception))
    ).execute(continuation);
  }

  private CompletableFuture<UUID> getUniqueIdOrGenerate(final User user, final String username) {
    if (user == null) {
      return identityGenerator.generateIdentity(username);
    }

    return completedFuture(user.getUniqueId());
  }
}
