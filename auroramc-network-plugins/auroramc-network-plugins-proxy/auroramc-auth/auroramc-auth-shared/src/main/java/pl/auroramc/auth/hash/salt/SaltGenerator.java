package pl.auroramc.auth.hash.salt;

public interface SaltGenerator {

  byte[] generateSalt(final int length);
}
