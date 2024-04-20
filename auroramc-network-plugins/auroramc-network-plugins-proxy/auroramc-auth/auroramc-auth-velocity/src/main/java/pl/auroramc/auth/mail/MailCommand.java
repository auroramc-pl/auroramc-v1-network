package pl.auroramc.auth.mail;

import static pl.auroramc.auth.message.MutableMessageVariableKey.EMAIL_PATH;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import pl.auroramc.auth.message.MutableMessageSource;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.mail")
@Command(name = "mail", aliases = "email")
public class MailCommand {

  private final Logger logger;
  private final MutableMessageSource messageSource;
  private final UserFacade userFacade;
  private final Pattern emailPattern;

  public MailCommand(
      final Logger logger,
      final MutableMessageSource messageSource,
      final UserFacade userFacade,
      final String unparsedEmailPattern) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.emailPattern = Pattern.compile(unparsedEmailPattern);
  }

  @Execute
  public CompletableFuture<MutableMessage> mail(
      final @Context Player player, final @Arg String email) {
    if (!emailPattern.matcher(email).matches()) {
      return messageSource.specifiedEmailIsInvalid.asCompletedFuture();
    }

    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenCompose(user -> handleUserEmail(user, email))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<MutableMessage> handleUserEmail(final User user, final String email) {
    if (user.isPremium()) {
      return messageSource.notAllowedBecauseOfPremiumAccount.asCompletedFuture();
    }

    if (!user.isRegistered()) {
      return messageSource.notAllowedBecauseOfNonRegisteredAccount.asCompletedFuture();
    }

    if (!user.isAuthenticated()) {
      return messageSource.notAllowedBecauseOfMissingAuthorization.asCompletedFuture();
    }

    if (Optional.ofNullable(user.getEmail()).filter(email::equalsIgnoreCase).isPresent()) {
      return messageSource.specifiedEmailIsTheSame.asCompletedFuture();
    }

    return userFacade
        .getUserByEmail(email)
        .thenCompose(persistedUser -> handleUserLogin(user, persistedUser, email));
  }

  private CompletableFuture<MutableMessage> handleUserLogin(
      final User user, final User persistedUser, final String email) {
    if (persistedUser != null) {
      return messageSource.specifiedEmailIsClaimed.asCompletedFuture();
    }

    user.setEmail(email);
    return userFacade
        .updateUser(user)
        .thenApply(state -> messageSource.emailHasBeenChanged.with(EMAIL_PATH, email))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
