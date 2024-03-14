package pl.auroramc.auth.authorization;

import static com.velocitypowered.api.event.EventTask.resumeWhenComplete;
import static com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.allowed;
import static com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult.denied;
import static pl.auroramc.auth.authorization.AuthorizationUtils.resolveCommand;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.proxy.Player;
import java.util.List;
import java.util.logging.Logger;
import pl.auroramc.auth.user.User;
import pl.auroramc.auth.user.UserFacade;

public class AuthorizationListener {

  private final Logger logger;
  private final UserFacade userFacade;
  private final List<String> defaultCommands;

  public AuthorizationListener(
      final Logger logger, final UserFacade userFacade, final List<String> defaultCommands
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.defaultCommands = defaultCommands;
  }

  @Subscribe
  public void onNonPermittedCommandExecution(
      final CommandExecuteEvent event, final Continuation continuation
  ) {
    if (event.getCommandSource() instanceof Player player) {
      resumeWhenComplete(
          userFacade.getUserByUniqueId(player.getUniqueId())
              .thenApply(User::isAuthenticated)
              .thenApply(
                  whetherIsAuthenticated ->
                      translateExecutionResult(
                          whetherIsAuthenticated, resolveCommand(event.getCommand())
                      )
              )
              .thenAccept(event::setResult)
              .exceptionally(exception -> delegateCaughtException(logger, exception))
      ).execute(continuation);
      return;
    }

    continuation.resume();
  }

  private CommandResult translateExecutionResult(
      final boolean whetherIsAuthenticated, final String attemptedCommandName
  ) {
    return defaultCommands.contains(attemptedCommandName) || whetherIsAuthenticated
        ? allowed()
        : denied();
  }
}
