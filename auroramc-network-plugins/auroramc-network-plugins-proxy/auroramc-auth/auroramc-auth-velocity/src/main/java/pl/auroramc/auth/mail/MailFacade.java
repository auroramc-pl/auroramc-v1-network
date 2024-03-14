package pl.auroramc.auth.mail;

import org.simplejavamail.api.email.Email;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.user.User;

public interface MailFacade {

  static MailFacade getEmailFacade(
      final MailConfig mailConfig,
      final MessageSource messageSource
  ) {
    return new MailService(mailConfig, messageSource);
  }

  Email getRecoveryEmail(final User user, final String recoveryCode);
}