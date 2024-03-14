package pl.auroramc.auth.command;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

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
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.changepassword")
@Route(name = "changepassword", aliases = "changepass")
public class PasswordChangeCommand {

  private final Logger logger;
  private final MessageSource messageSource;
  private final HashingStrategy hashingStrategy;
  private final PasswordController passwordController;

  public PasswordChangeCommand(
      final Logger logger,
      final MessageSource messageSource,
      final HashingStrategy hashingStrategy,
      final PasswordController passwordController
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.hashingStrategy = hashingStrategy;
    this.passwordController = passwordController;
  }

  @Execute
  public CompletableFuture<MutableMessage> changePassword(
      final Player player,
      final @Arg String oldPassword,
      final @Arg String newPassword
  ) {
    return passwordController.validateChangeOfPassword(
        player, newPassword, (subject, user, password) ->
            handlePasswordChange(subject, user, oldPassword, newPassword)
    ).exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> handlePasswordChange(
      final Player player, final User user, final String oldPassword, final String newPassword) {
    if (user.isPremium()) {
      return completedFuture(messageSource.notAllowedBecauseOfPremiumAccount);
    }

    if (!user.isRegistered()) {
      return completedFuture(messageSource.notAllowedBecauseOfNonRegisteredAccount);
    }

    if (!user.isAuthenticated()) {
      return completedFuture(messageSource.notAllowedBecauseOfMissingAuthorization);
    }

    if (!hashingStrategy.verifyPassword(oldPassword, user.getPassword())) {
      return completedFuture(messageSource.specifiedPasswordIsInvalid);
    }

    if (hashingStrategy.verifyPassword(newPassword, user.getPassword())) {
      return completedFuture(messageSource.specifiedPasswordIsSame);
    }

    return passwordController.submitChangeOfPassword(player, user, newPassword)
        .thenApply(state -> messageSource.passwordChanged);
  }
}