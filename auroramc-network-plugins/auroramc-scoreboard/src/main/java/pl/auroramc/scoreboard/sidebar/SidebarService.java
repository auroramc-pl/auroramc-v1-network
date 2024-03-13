package pl.auroramc.scoreboard.sidebar;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Bukkit.getPlayer;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class SidebarService implements SidebarFacade {

  private final Map<UUID, FastBoard> sidebars;

  SidebarService() {
    this.sidebars = new HashMap<>();
  }

  @Override
  public FastBoard resolveSidebarByUniqueId(final UUID uniqueId) {
    return Optional.ofNullable(getSidebarByUniqueId(uniqueId))
        .orElseGet(() -> {
          createSidebarByUniqueId(uniqueId);
          return getSidebarByUniqueId(uniqueId);
        });
  }

  @Override
  public FastBoard getSidebarByUniqueId(final UUID uniqueId) {
    return sidebars.get(uniqueId);
  }

  @Override
  public void createSidebarByUniqueId(final UUID uniqueId) {
    sidebars.put(uniqueId, new FastBoard(checkNotNull(getPlayer(uniqueId))));
  }

  @Override
  public void deleteSidebarByUniqueId(final UUID uniqueId) {
    Optional.ofNullable(sidebars.remove(uniqueId)).ifPresent(FastBoard::delete);
  }
}
