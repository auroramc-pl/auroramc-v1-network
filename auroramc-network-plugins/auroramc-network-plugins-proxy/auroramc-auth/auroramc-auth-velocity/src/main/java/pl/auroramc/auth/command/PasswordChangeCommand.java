package pl.auroramc.auth.command;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.auth.hash.HashingStrategy;
import pl.auroramc.auth.message.MutableMessageSource;
import pl.auroramc.auth.user.User;
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.changepassword")
@Command(name = "changepassword", aliases = "changepass")
public class PasswordChangeCommand {

  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final HashingStrategy hashingStrategy;
  private final PasswordController passwordController;

  public PasswordChangeCommand(
      final Logger logger,
      final MutableMessageSource messageSource,
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
      final @Context Player player,
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
      return messageSource.notAllowedBecauseOfPremiumAccount
          .asCompletedFuture();
    }

    if (!user.isRegistered()) {
      return messageSource.notAllowedBecauseOfNonRegisteredAccount
          .asCompletedFuture();
    }

    if (!user.isAuthenticated()) {
      return messageSource.notAllowedBecauseOfMissingAuthorization
          .asCompletedFuture();
    }

    if (!hashingStrategy.verifyPassword(oldPassword, user.getPassword())) {
      return messageSource.specifiedPasswordIsInvalid
          .asCompletedFuture();
    }

    if (hashingStrategy.verifyPassword(newPassword, user.getPassword())) {
      return messageSource.specifiedPasswordIsSame
          .asCompletedFuture();
    }

    return passwordController.submitChangeOfPassword(player, user, newPassword)
        .thenApply(state -> messageSource.passwordChanged);
  }
}
