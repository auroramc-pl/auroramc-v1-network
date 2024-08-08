package pl.auroramc.scoreboard.sidebar;

import static java.util.stream.Stream.concat;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.messages.i18n.BukkitMessageFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.messages.viewer.BukkitViewer;
import pl.auroramc.messages.viewer.BukkitViewerFacade;
import pl.auroramc.scoreboard.ScoreboardConfig;
import pl.auroramc.scoreboard.message.ScoreboardMessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

class DefaultSidebarRenderer implements SidebarRenderer {

  private final ScoreboardConfig scoreboardConfig;
  private final ScoreboardMessageSource messageSource;
  private final BukkitMessageFacade messageFacade;
  private final BukkitMessageCompiler messageCompiler;
  private final BukkitViewerFacade viewerFacade;
  private final SidebarFacade sidebarFacade;
  private final Set<SidebarComponent<?>> sidebarComponents;

  DefaultSidebarRenderer(
      final ScoreboardConfig scoreboardConfig,
      final ScoreboardMessageSource messageSource,
      final BukkitMessageFacade messageFacade,
      final BukkitMessageCompiler messageCompiler,
      final BukkitViewerFacade viewerFacade,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponent<?>> sidebarComponents) {
    this.scoreboardConfig = scoreboardConfig;
    this.messageSource = messageSource;
    this.messageFacade = messageFacade;
    this.messageCompiler = messageCompiler;
    this.viewerFacade = viewerFacade;
    this.sidebarFacade = sidebarFacade;
    this.sidebarComponents = sidebarComponents;
  }

  @Override
  public void render(final Player player) {
    final BukkitViewer viewer = viewerFacade.getViewerByUniqueId(player.getUniqueId());

    final FastBoard sidebar = sidebarFacade.resolveSidebarByUniqueId(player.getUniqueId());
    sidebar.updateTitle(
        messageCompiler
            .compile(viewer, messageFacade.getMessage(viewer, messageSource.title))
            .getComponent());
    sidebar.updateLines(
        concat(
                scoreboardConfig.lines.stream()
                    .map(
                        messageKey ->
                            messageCompiler.compile(
                                viewer, messageFacade.getMessage(viewer, messageKey))),
                sidebarComponents.stream().flatMap(component -> component.render(player).stream()))
            .map(CompiledMessage::getComponent)
            .toArray(Component[]::new));
  }
}
