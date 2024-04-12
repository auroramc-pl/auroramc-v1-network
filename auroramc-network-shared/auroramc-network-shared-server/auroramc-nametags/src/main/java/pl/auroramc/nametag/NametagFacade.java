package pl.auroramc.nametag;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.auroramc.messages.message.compiler.CompiledMessage;
import pl.auroramc.nametag.context.NametagContextFacade;

public interface NametagFacade {

  static NametagFacade getNametagFacade(final NametagContextFacade nametagContextFacade) {
    return new NametagService(nametagContextFacade);
  }

  default void belowName(final Player player, final CompiledMessage message) {
    belowName(player, message.getComponent());
  }

  void belowName(final Player player, final Component belowName);

  void inject(final Player player);

  void update(final Player player);

  void updateServerWide();
}
