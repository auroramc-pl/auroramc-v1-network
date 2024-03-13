package pl.auroramc.scoreboard.sidebar;

import java.util.Set;
import org.bukkit.entity.Player;
import pl.auroramc.scoreboard.message.MessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponentKyori;

public interface SidebarRenderer {

  static SidebarRenderer getSidebarRenderer(
      final MessageSource messageSource,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponentKyori<?>> sidebarComponents
  ) {
    return new DefaultSidebarRenderer(messageSource, sidebarFacade, sidebarComponents);
  }

  void render(final Player viewer);
}
