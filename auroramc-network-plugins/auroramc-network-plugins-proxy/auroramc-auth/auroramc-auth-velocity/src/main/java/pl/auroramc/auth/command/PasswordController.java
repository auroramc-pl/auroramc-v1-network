package pl.auroramc.auth.command;

import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.proxy.Player;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import panda.std.function.TriFunction;
import pl.auroramc.auth.hash.HashingStrategy;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.password.PasswordValidator;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserController;
import pl.auroramc.auth.user.UserFacade;
import pl.auroramc.commons.message.MutableMessage;

public class PasswordController {

  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final UserController userController;
  private final HashingStrategy hashingStrategy;
  private final PasswordValidator passwordValidator;

  public PasswordController(
      final Logger logger,
      final MessageSource messageSource,
      final UserFacade userFacade,
      final UserController userController,
      final HashingStrategy hashingStrategy,
      final PasswordValidator passwordValidator
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.userController = userController;
    this.hashingStrategy = hashingStrategy;
    this.passwordValidator = passwordValidator;
  }

  public CompletableFuture<MutableMessage> validateChangeOfPassword(
      final Player player,
      final String password,
      final TriFunction<Player, User, String, CompletableFuture<MutableMessage>> processor
  ) {
    if (!passwordValidator.validatePassword(password)) {
      return messageSource.specifiedPasswordIsUnsafe
          .asCompletedFuture();
    }

    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenCompose(user -> processor.apply(player, user, password))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  public CompletableFuture<Void> submitChangeOfPassword(
      final Player player, final User user, final String password
  ) {
    user.setPassword(hashingStrategy.hashPassword(password));
    return userFacade.updateUser(user)
        .thenAccept(state -> user.setAuthenticated(true))
        .thenAccept(state -> userController.redirectUser(player, user));
  }
}
