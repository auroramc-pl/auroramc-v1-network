package pl.auroramc.bounties.bounty;

import java.nio.file.Path;

public final class BountyFacadeFactory {

  private BountyFacadeFactory() {}

  public static BountyFacade getBountyFacade(
      final ClassLoader parentClassLoader, final Path bountiesDirectoryPath) {
    return new BountyService(parentClassLoader, bountiesDirectoryPath);
  }
}
