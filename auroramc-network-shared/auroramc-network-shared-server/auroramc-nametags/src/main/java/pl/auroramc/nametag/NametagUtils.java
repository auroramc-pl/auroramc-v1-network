package pl.auroramc.nametag;

import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

final class NametagUtils {

  private NametagUtils() {}

  static ServerPlayerConnection getOutboundConnection(final Player player) {
    return ((CraftPlayer) checkNotNull(player)).getHandle().connection;
  }
}
