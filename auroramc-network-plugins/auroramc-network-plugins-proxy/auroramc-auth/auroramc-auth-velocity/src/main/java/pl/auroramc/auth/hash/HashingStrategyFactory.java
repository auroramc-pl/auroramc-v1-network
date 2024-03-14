package pl.auroramc.auth.hash;

import pl.auroramc.auth.hash.salt.SaltGenerator;

public final class HashingStrategyFactory {

  private HashingStrategyFactory() {

  }

  public static HashingStrategy getHashingStrategy(final SaltGenerator saltGenerator) {
    return new BCryptHashingStrategy(saltGenerator);
  }
}
