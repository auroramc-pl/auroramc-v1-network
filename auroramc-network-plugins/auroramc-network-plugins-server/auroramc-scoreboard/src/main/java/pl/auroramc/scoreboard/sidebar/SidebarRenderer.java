package pl.auroramc.scoreboard.sidebar;

import java.util.Set;
import org.bukkit.entity.Player;
import pl.auroramc.messages.i18n.BukkitMessageFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.viewer.BukkitViewerFacade;
import pl.auroramc.scoreboard.ScoreboardConfig;
import pl.auroramc.scoreboard.message.ScoreboardMessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

public interface SidebarRenderer {

  static SidebarRenderer getSidebarRenderer(
      final ScoreboardConfig scoreboardConfig,
      final ScoreboardMessageSource messageSource,
      final BukkitMessageFacade messageFacade,
      final BukkitMessageCompiler messageCompiler,
      final BukkitViewerFacade viewerFacade,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponent<?>> sidebarComponents) {
    return new DefaultSidebarRenderer(
        scoreboardConfig,
        messageSource,
        messageFacade,
        messageCompiler,
        viewerFacade,
        sidebarFacade,
        sidebarComponents);
  }

  void render(final Player player);
}
