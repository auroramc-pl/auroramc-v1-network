package pl.auroramc.auth.mail;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.mail")
@Route(name = "mail", aliases = "email")
public class MailCommand {

  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final Pattern emailPattern;

  public MailCommand(
      final Logger logger,
      final MessageSource messageSource,
      final UserFacade userFacade,
      final String unparsedEmailPattern
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.emailPattern = Pattern.compile(unparsedEmailPattern);
  }

  @Execute
  public CompletableFuture<MutableMessage> mail(
      final Player player, final @Arg String email
  ) {
    if (!emailPattern.matcher(email).matches()) {
      return completedFuture(messageSource.specifiedEmailIsInvalid);
    }

    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenCompose(user -> handleUserEmail(user, email))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> handleUserEmail(
      final User user, final String email
  ) {
    if (user.isPremium()) {
      return completedFuture(messageSource.notAllowedBecauseOfPremiumAccount);
    }

    if (!user.isRegistered()) {
      return completedFuture(messageSource.notAllowedBecauseOfNonRegisteredAccount);
    }

    if (!user.isAuthenticated()) {
      return completedFuture(messageSource.notAllowedBecauseOfMissingAuthorization);
    }

    if (
        Optional.ofNullable(user.getEmail())
            .filter(email::equalsIgnoreCase)
            .isPresent()
    ) {
      return completedFuture(messageSource.specifiedEmailIsTheSame);
    }

    return userFacade.getUserByEmail(email)
        .thenCompose(persistedUser -> handleUserLogin(user, persistedUser, email));
  }

  private CompletableFuture<MutableMessage> handleUserLogin(
      final User user, final User persistedUser, final String email
  ) {
    if (persistedUser != null) {
      return completedFuture(messageSource.specifiedEmailIsClaimed);
    }

    user.setEmail(email);
    return userFacade.updateUser(user)
        .exceptionally(exception -> delegateCaughtException(logger, exception))
        .thenApply(state -> messageSource.emailHasBeenChanged.with("email", email));
  }
}