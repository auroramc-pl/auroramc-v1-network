package pl.auroramc.commons.integration;

import org.bukkit.Server;

public abstract class ExternalIntegrationDelegate implements ExternalIntegration {

  private final String dependencyName;

  protected ExternalIntegrationDelegate(final String dependencyName) {
    this.dependencyName = dependencyName;
  }

  @Override
  public boolean isSupportedEnvironment(final Server server) {
    return server.getPluginManager().isPluginEnabled(dependencyName);
  }
}
