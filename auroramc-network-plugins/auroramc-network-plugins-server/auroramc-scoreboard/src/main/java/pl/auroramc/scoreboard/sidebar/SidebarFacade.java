package pl.auroramc.scoreboard.sidebar;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.UUID;

public interface SidebarFacade {

  static SidebarFacade getSidebarFacade() {
    return new SidebarService();
  }

  FastBoard resolveSidebarByUniqueId(final UUID uniqueId);

  FastBoard getSidebarByUniqueId(final UUID uniqueId);

  void createSidebarByUniqueId(final UUID uniqueId);

  void deleteSidebarByUniqueId(final UUID uniqueId);
}
