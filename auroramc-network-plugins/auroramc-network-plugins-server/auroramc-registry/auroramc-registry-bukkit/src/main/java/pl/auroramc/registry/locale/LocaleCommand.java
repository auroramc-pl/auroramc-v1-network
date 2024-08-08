package pl.auroramc.registry.locale;

import static java.lang.Boolean.TRUE;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.messages.i18n.Message.message;
import static pl.auroramc.registry.message.RegistryMessageSourcePaths.LOCALE_PATH;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.bukkit.event.BukkitEventPublisher;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.messages.i18n.Message;
import pl.auroramc.registry.message.RegistryMessageSource;
import pl.auroramc.registry.settings.Settings;
import pl.auroramc.registry.settings.SettingsController;
import pl.auroramc.registry.settings.SettingsFacade;

@Permission("auroramc.registry.locale")
@Command(
    name = "locale",
    aliases = {"language", "lang"})
public class LocaleCommand {

  private final RegistryMessageSource registryMessageSource;
  private final BukkitEventPublisher eventPublisher;
  private final SettingsFacade settingsFacade;
  private final SettingsController settingsController;

  public LocaleCommand(
      final RegistryMessageSource registryMessageSource,
      final BukkitEventPublisher eventPublisher,
      final SettingsFacade settingsFacade,
      final SettingsController settingsController) {
    this.registryMessageSource = registryMessageSource;
    this.eventPublisher = eventPublisher;
    this.settingsFacade = settingsFacade;
    this.settingsController = settingsController;
  }

  @Execute
  public CompletableFuture<Message> setLocale(
      final @Context Player player, final @Arg Locale locale) {
    return settingsController
        .getOrCreateSettings(player)
        .thenCompose(settings -> setLocale(settings, player, locale))
        .thenApply(
            modified ->
                message(
                        TRUE.equals(modified)
                            ? registryMessageSource.localeChanged
                            : registryMessageSource.localeIsSame)
                    .placeholder(LOCALE_PATH, locale))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<Boolean> setLocale(
      final Settings settings, final Player player, final Locale locale) {
    if (settings.getLocale() != null && settings.getLocale().equals(locale)) {
      return completedFuture(false);
    }

    settings.setLocale(locale);
    return settingsFacade
        .updateSettings(settings)
        .thenAccept(state -> settingsController.applySettings(player, settings))
        .thenAccept(
            state ->
                eventPublisher.publish(
                    new LocaleChangedEvent(player, settings.getLocale(), locale)))
        .thenApply(state -> true)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
