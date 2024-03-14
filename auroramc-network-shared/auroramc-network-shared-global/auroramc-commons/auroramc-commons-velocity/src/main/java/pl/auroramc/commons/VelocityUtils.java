package pl.auroramc.commons;

import com.velocitypowered.api.proxy.ProxyServer;

public final class VelocityUtils {

  private VelocityUtils() {

  }

  public static void registerListeners(
      final Object plugin, final ProxyServer server, final Object... bunchOfListeners
  ) {
    for (final Object listener : bunchOfListeners) {
      server.getEventManager().register(plugin, listener);
    }
  }
}
