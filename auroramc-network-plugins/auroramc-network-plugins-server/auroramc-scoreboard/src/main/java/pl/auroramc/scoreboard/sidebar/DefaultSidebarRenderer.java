package pl.auroramc.scoreboard.sidebar;

import static java.util.stream.Stream.concat;

import fr.mrmicky.fastboard.adventure.FastBoard;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.commons.message.BukkitMutableMessage;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.scoreboard.message.MutableMessageSource;
import pl.auroramc.scoreboard.sidebar.component.SidebarComponentKyori;

class DefaultSidebarRenderer implements SidebarRenderer {

  private final MutableMessageSource messageSource;
  private final SidebarFacade sidebarFacade;
  private final Set<SidebarComponentKyori<?>> sidebarComponents;

  DefaultSidebarRenderer(
      final MutableMessageSource messageSource,
      final SidebarFacade sidebarFacade,
      final Set<SidebarComponentKyori<?>> sidebarComponents) {
    this.messageSource = messageSource;
    this.sidebarFacade = sidebarFacade;
    this.sidebarComponents = sidebarComponents;
  }

  @Override
  public void render(final Player viewer) {
    final FastBoard sidebar = sidebarFacade.resolveSidebarByUniqueId(viewer.getUniqueId());
    sidebar.updateTitle(getCompiledMessageWithPlaceholders(viewer, messageSource.title));
    sidebar.updateLines(
        BukkitMutableMessage.of(
                concat(
                        messageSource.lines.stream(),
                        sidebarComponents.stream()
                            .map(sidebarComponent -> sidebarComponent.render(viewer)))
                    .collect(MutableMessage.collector()))
            .withTargetedPlaceholders(viewer)
            .compileChildren());
  }

  private Component getCompiledMessageWithPlaceholders(
      final Player viewer, final MutableMessage message) {
    return BukkitMutableMessage.of(message).withTargetedPlaceholders(viewer).compile();
  }
}
