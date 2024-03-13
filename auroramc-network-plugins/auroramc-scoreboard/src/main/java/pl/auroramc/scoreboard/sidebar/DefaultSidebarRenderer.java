package pl.auroramc.scoreboard.sidebar;

import static me.clip.placeholderapi.PlaceholderAPI.setPlaceholders;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.scoreboard.message.MessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponentKyori;

class DefaultSidebarRenderer implements SidebarRenderer {

  private final MessageSource messageSource;
  private final SidebarFacade sidebarFacade;
  private final Set<SidebarComponentKyori<?>> sidebarComponents;

  DefaultSidebarRenderer(
      final MessageSource messageSource,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponentKyori<?>> sidebarComponents
  ) {
    this.messageSource = messageSource;
    this.sidebarFacade = sidebarFacade;
    this.sidebarComponents = sidebarComponents;
  }

  @Override
  public void render(final Player viewer) {
    final FastBoard sidebar = sidebarFacade.resolveSidebarByUniqueId(viewer.getUniqueId());
    sidebar.updateTitle(getCompiledMessageWithPlaceholders(viewer, messageSource.title));

    final List<Component> aggregatedLinesOfSidebar = new ArrayList<>(getDefaultLinesOfSidebar(viewer));
    for (final SidebarComponentKyori<?> sidebarComponent : sidebarComponents) {
      aggregatedLinesOfSidebar.addAll(sidebarComponent.render(viewer));
    }

    sidebar.updateLines(aggregatedLinesOfSidebar);
  }

  private List<Component> getDefaultLinesOfSidebar(final Player viewer) {
    return messageSource.lines.stream()
        .map(line -> getCompiledMessageWithPlaceholders(viewer, line))
        .toList();
  }

  private Component getCompiledMessageWithPlaceholders(
      final Player viewer, final MutableMessage template
  ) {
    return miniMessage().deserialize(setPlaceholders(viewer, template.getTemplate()));
  }
}
