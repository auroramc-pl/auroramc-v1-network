package pl.auroramc.auth.hash.salt;

public final class SaltGeneratorFactory {

  private SaltGeneratorFactory() {}

  public static SaltGenerator getSaltGenerator() {
    return new SecureSaltGenerator();
  }
}
