package pl.auroramc.scoreboard.sidebar.component;

import java.util.List;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;

interface SidebarComponent<V, Y> {

  List<Component> render(final V viewer, final @Nullable Y value);

  List<Component> render(final V viewer);
}
