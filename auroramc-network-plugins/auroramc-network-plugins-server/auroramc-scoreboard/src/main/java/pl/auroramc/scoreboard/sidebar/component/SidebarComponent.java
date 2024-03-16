package pl.auroramc.scoreboard.sidebar.component;

import javax.annotation.Nullable;
import pl.auroramc.commons.message.MutableMessage;

interface SidebarComponent<V, Y> {

  MutableMessage render(final V viewer, final @Nullable Y value);

  MutableMessage render(final V viewer);
}
