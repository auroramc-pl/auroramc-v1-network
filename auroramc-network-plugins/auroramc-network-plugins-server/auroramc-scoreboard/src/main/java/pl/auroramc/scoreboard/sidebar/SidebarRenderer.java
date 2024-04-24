package pl.auroramc.scoreboard.sidebar;

import java.util.Set;
import org.bukkit.entity.Player;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.scoreboard.message.MessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponent;

public interface SidebarRenderer {

  static SidebarRenderer getSidebarRenderer(
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponent<?>> sidebarComponents) {
    return new DefaultSidebarRenderer(
        messageSource, messageCompiler, sidebarFacade, sidebarComponents);
  }

  void render(final Player viewer);
}
