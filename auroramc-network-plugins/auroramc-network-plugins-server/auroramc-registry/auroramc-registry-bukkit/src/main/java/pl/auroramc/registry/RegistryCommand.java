package pl.auroramc.registry;

import static pl.auroramc.commons.concurrent.CompletableFutureUtils.delegateCaughtException;
import static pl.auroramc.messages.i18n.Message.message;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.configs.exception.OkaeriException;
import pl.auroramc.integrations.configs.message.IntegrationsMessageSource;
import pl.auroramc.messages.i18n.BukkitMessageFacade;
import pl.auroramc.messages.i18n.Message;

@Permission("auroramc.registry.registry")
@Command(name = "registry")
class RegistryCommand {

  private final IntegrationsMessageSource messageSource;
  private final BukkitMessageFacade messageFacade;

  RegistryCommand(
      final IntegrationsMessageSource messageSource, final BukkitMessageFacade messageFacade) {
    this.messageSource = messageSource;
    this.messageFacade = messageFacade;
  }

  @Execute(name = "reload")
  public Message reload() {
    try {
      messageFacade.refresh();
      return message(messageSource.messagesRefreshSuccess);
    } catch (final OkaeriException exception) {
      delegateCaughtException(exception);
      return message(messageSource.messagesRefreshFailure);
    }
  }
}
