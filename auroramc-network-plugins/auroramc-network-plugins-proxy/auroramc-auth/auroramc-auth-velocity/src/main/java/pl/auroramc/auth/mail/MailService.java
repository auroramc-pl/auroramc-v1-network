package pl.auroramc.auth.mail;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.email.EmailBuilder;
import pl.auroramc.auth.message.MessageSource;
import pl.auroramc.auth.user.User;
import pl.auroramc.commons.message.MutableMessage;

class MailService implements MailFacade {

  private final MailConfig mailConfig;
  private final MessageSource messageSource;
  private final MutableMessage recoveryEmailTemplate;

  MailService(final MailConfig mailConfig, final MessageSource messageSource) {
    this.mailConfig = mailConfig;
    this.messageSource = messageSource;
    this.recoveryEmailTemplate = getRecoveryEmailTemplate();
  }

  @Override
  public Email getRecoveryEmail(final User user, final String recoveryCode) {
    if (user.getEmail() == null) {
      throw new MailDeliveryException("Could not deliver email to user (%s) without email address".formatted(user.getUniqueId()));
    }

    return EmailBuilder.startingBlank()
        .from(mailConfig.name, mailConfig.address)
        .to(user.getUsername(), user.getEmail())
        .withSubject(messageSource.recoveryEmailSubject.getTemplate())
        .withHTMLText(recoveryEmailTemplate
            .with("username", user.getUsername())
            .with("recoveryCode", recoveryCode)
            .getTemplate())
        .buildEmail();
  }

  private MutableMessage getRecoveryEmailTemplate() {
    try (final InputStream inputStream = getClass().getResourceAsStream("/recovery.html")) {
      if (inputStream == null) {
        throw new MailRetrieveException(
            "Could not load recovery email template"
        );
      }

      final byte[] buffer = new byte[inputStream.available()];
      final int bytes = inputStream.read(buffer);
      if (bytes == 0) {
        throw new MailRetrieveException(
            "Could not load recovery email template, since it is empty"
        );
      }

      return MutableMessage.of(new String(buffer, UTF_8));
    } catch (final Exception exception) {
      throw new MailDeliveryException(
          "Could not load recovery email template",
          exception
      );
    }
  }
}
