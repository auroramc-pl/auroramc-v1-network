package pl.auroramc.auth.recovery;

import static pl.auroramc.auth.recovery.RecoveryUtils.generateRecoveryCode;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.velocitypowered.api.proxy.Player;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.util.logging.Logger;
import org.simplejavamail.api.mailer.Mailer;
import pl.auroramc.auth.mail.MailFacade;
import pl.auroramc.auth.user.UserFacade;

@Permission("auroramc.auth.recovery")
@Route(name = "recovery", aliases = {"recover", "odzyskaj"})
public class RecoveryCommand {

  private final Logger logger;
  private final Mailer mailer;
  private final MailFacade mailFacade;
  private final UserFacade userFacade;

  public RecoveryCommand(
      final Logger logger,
      final Mailer mailer,
      final MailFacade mailFacade,
      final UserFacade userFacade
  ) {
    this.logger = logger;
    this.mailer = mailer;
    this.mailFacade = mailFacade;
    this.userFacade = userFacade;
  }

  @Execute
  public void recovery(final Player player) {
    userFacade.getUserByUniqueId(player.getUniqueId())
        .thenAccept(user -> mailer.sendMail(mailFacade.getRecoveryEmail(user, generateRecoveryCode())))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
