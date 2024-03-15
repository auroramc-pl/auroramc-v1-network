package pl.auroramc.auth.command;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.message.MutableMessage.empty;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.auth.hash.HashingStrategy;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.unregister")
@Route(name = "unregister", aliases = {"unreg", "odrejestruj"})
public class UnregisterCommand {

  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final HashingStrategy hashingStrategy;

  public UnregisterCommand(
      final Logger logger,
      final MessageSource messageSource,
      final UserFacade userFacade,
      final HashingStrategy hashingStrategy
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.hashingStrategy = hashingStrategy;
  }

  @Execute
  public CompletableFuture<MutableMessage> unregister(
      final Player player, final @Arg String password
  ) {
    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenCompose(user -> handleUserUnregistration(player, user, password))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> handleUserUnregistration(
      final Player player, final User user, final String password
  ) {
    if (user.isPremium()) {
      return messageSource.notAllowedBecauseOfPremiumAccount
          .asCompletedFuture();
    }

    if (!user.isRegistered()) {
      return messageSource.notAllowedBecauseOfRegisteredAccount
          .asCompletedFuture();
    }

    if (!user.isAuthenticated()) {
      return messageSource.notAllowedBecauseOfMissingAuthorization
          .asCompletedFuture();
    }

    if (!hashingStrategy.verifyPassword(password, user.getPassword())) {
      return messageSource.specifiedPasswordIsInvalid
          .asCompletedFuture();
    }

    user.setPassword(null);
    return userFacade.updateUser(user)
        .thenAccept(state -> user.setAuthenticated(false))
        .thenAccept(state -> player.disconnect(messageSource.unregisterAccount.compile()))
        .thenApply(state -> empty());
  }
}
