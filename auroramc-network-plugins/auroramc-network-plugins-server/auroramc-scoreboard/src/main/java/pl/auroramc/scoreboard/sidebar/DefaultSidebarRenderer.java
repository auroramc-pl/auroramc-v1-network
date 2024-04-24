package pl.auroramc.scoreboard.sidebar;

import static java.util.stream.Stream.concat;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.scoreboard.message.MessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

class DefaultSidebarRenderer implements SidebarRenderer {

  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final SidebarFacade sidebarFacade;
  private final Set<SidebarComponent<?>> sidebarComponents;

  DefaultSidebarRenderer(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponent<?>> sidebarComponents) {
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.sidebarFacade = sidebarFacade;
    this.sidebarComponents = sidebarComponents;
  }

  @Override
  public void render(final Player viewer) {
    final FastBoard sidebar = sidebarFacade.resolveSidebarByUniqueId(viewer.getUniqueId());
    sidebar.updateTitle(messageCompiler.compile(viewer, messageSource.title).getComponent());
    sidebar.updateLines(
        concat(
                messageSource.lines.stream()
                    .map(message -> messageCompiler.compile(viewer, message)),
                sidebarComponents.stream()
                    .flatMap(component -> component.render(viewer).stream()))
            .map(CompiledMessage::getComponent)
            .toArray(Component[]::new));
  }
}
