package pl.auroramc.registry.settings;

import static java.util.Locale.forLanguageTag;
import static java.util.concurrent.CompletableFuture.completedFuture;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.BukkitViewerFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class SettingsController {

  private final UserFacade userFacade;
  private final BukkitViewerFacade viewerFacade;
  private final SettingsFacade settingsFacade;

  public SettingsController(
      final UserFacade userFacade,
      final BukkitViewerFacade viewerFacade,
      final SettingsFacade settingsFacade) {
    this.userFacade = userFacade;
    this.viewerFacade = viewerFacade;
    this.settingsFacade = settingsFacade;
  }

  public CompletableFuture<Settings> getOrCreateSettings(final Player player) {
    return userFacade
        .getUserByUniqueId(player.getUniqueId())
        .thenCompose(
            user ->
                settingsFacade
                    .getSettingsByUserId(user.getId())
                    .thenApply(settings -> new UserAndSettingsPair(user, settings)))
        .thenCompose(
            pair -> {
              final Settings settings = pair.settings();
              if (settings != null) {
                return completedFuture(settings);
              }
              return createSettings(pair.user(), player.locale());
            })
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  public void applySettings(final Player player, final Settings settings) {
    final BukkitViewer viewer = viewerFacade.getOrCreateViewerByUniqueId(player.getUniqueId());
    viewer.setCurrentLocale(settings.getLocale());
  }

  private CompletableFuture<Settings> createSettings(final User user, final Locale clientLocale) {
    final Settings newSettings = new Settings(null, user.getId(), getStrippedLocale(clientLocale));
    return settingsFacade.createSettings(newSettings).thenApply(state -> newSettings);
  }

  private Locale getStrippedLocale(final Locale originalLocale) {
    return forLanguageTag(originalLocale.getLanguage());
  }

  private record UserAndSettingsPair(User user, Settings settings) {}
}
