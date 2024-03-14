package pl.auroramc.auth.command;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import pl.auroramc.auth.hash.HashingStrategy;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.timeout.TimeoutFacade;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserController;
import pl.auroramc.auth.user.UserFacade;
import pl.auroramc.commons.message.MutableMessage;

@Permission("auroramc.auth.login")
@Route(name = "login", aliases = {"l", "zaloguj"})
public class LoginCommand {

  private static final int MAXIMUM_LOGIN_ATTEMPTS = 3;
  private static final int DEFAULT_LOGIN_ATTEMPTS = 1;
  private final Logger logger;
  private final MessageSource messageSource;
  private final UserFacade userFacade;
  private final UserController userController;
  private final HashingStrategy hashingStrategy;
  private final TimeoutFacade timeoutFacade;
  private final LoadingCache<UUID, Integer> loginAttemptsCache;

  public LoginCommand(
      final Logger logger,
      final MessageSource messageSource,
      final UserFacade userFacade,
      final UserController userController,
      final HashingStrategy hashingStrategy,
      final TimeoutFacade timeoutFacade
  ) {
    this.logger = logger;
    this.messageSource = messageSource;
    this.userFacade = userFacade;
    this.userController = userController;
    this.hashingStrategy = hashingStrategy;
    this.timeoutFacade = timeoutFacade;
    this.loginAttemptsCache = Caffeine.newBuilder()
        .expireAfterWrite(ofSeconds(20))
        .build(key -> DEFAULT_LOGIN_ATTEMPTS);
  }

  @Execute
  public CompletableFuture<MutableMessage> login(
      final Player player, final @Arg String password
  ) {
    return userFacade.getUserByUniqueId(player.getUniqueId())
        .thenApply(user -> handleUserLogin(player, user, password))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private MutableMessage handleUserLogin(
      final Player player, final User user, final String password
  ) {
    if (user.isPremium()) {
      return messageSource.notAllowedBecauseOfPremiumAccount;
    }

    if (user.isAuthenticated()) {
      return messageSource.notAllowedBecauseOfAuthorization;
    }

    if (!user.isRegistered()) {
      return messageSource.notAllowedBecauseOfNonRegisteredAccount;
    }

    final int attemptsCount = loginAttemptsCache.get(player.getUniqueId());
    if (attemptsCount >= MAXIMUM_LOGIN_ATTEMPTS) {
      loginAttemptsCache.invalidate(player.getUniqueId());
      player.disconnect(messageSource.tooManyLoginAttempts.compile());
      return null;
    }

    loginAttemptsCache.put(player.getUniqueId(), attemptsCount + 1);
    if (!hashingStrategy.verifyPassword(password, user.getPassword())) {
      return messageSource.specifiedPasswordIsInvalid;
    }

    user.setAuthenticated(true);
    userController.redirectUser(player, user);

    timeoutFacade.ditchCountdown(player.getUniqueId());
    return messageSource.loginSuccessful;
  }
}
