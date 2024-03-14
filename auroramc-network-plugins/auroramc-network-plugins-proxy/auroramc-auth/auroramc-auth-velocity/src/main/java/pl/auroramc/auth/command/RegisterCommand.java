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
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.timeout.TimeoutFacade;
import pl.auroramc.auth.user.User;
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.register")
@Route(name = "register", aliases = {"reg", "rejestracja", "zarejestruj"})
public class RegisterCommand {

  private final Logger logger;
  private final MessageSource messageSource;
  private final TimeoutFacade timeoutFacade;
  private final PasswordController passwordController;

  public RegisterCommand(
      final Logger logger,
      final MessageSource messageSource,
      final TimeoutFacade timeoutFacade,
      final PasswordController passwordController
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.timeoutFacade = timeoutFacade;
    this.passwordController = passwordController;
  }

  @Execute
  public CompletableFuture<MutableMessage> register(
      final Player player, final @Arg String password, final @Arg String repeatedPassword
  ) {
    if (!password.equals(repeatedPassword)) {
      return completedFuture(messageSource.specifiedPasswordsDiffers);
    }

    return passwordController.validateChangeOfPassword(player, password, this::handleUserRegistration)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> handleUserRegistration(
      final Player player, final User user, final String newPassword
  ) {
    if (user.isPremium()) {
      return completedFuture(messageSource.notAllowedBecauseOfPremiumAccount);
    }

    if (user.isRegistered()) {
      return completedFuture(messageSource.notAllowedBecauseOfRegisteredAccount);
    }

    timeoutFacade.ditchCountdown(player.getUniqueId());
    return passwordController.submitChangeOfPassword(player, user, newPassword)
        .thenApply(state -> messageSource.registeredAccount);
  }
}
