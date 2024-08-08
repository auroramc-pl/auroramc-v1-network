package pl.auroramc.scoreboard.sidebar.component;

import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pl.auroramc.messages.message.compiler.CompiledMessage;

public interface SidebarComponent<Y> {

  List<CompiledMessage> render(final Player player, final @Nullable Y value);

  List<CompiledMessage> render(final Player player);
}
