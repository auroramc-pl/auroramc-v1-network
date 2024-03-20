package pl.auroramc.auth.hash.salt;

import java.security.SecureRandom;

class SecureSaltGenerator implements SaltGenerator {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  SecureSaltGenerator() {}

  @Override
  public byte[] generateSalt(final int length) {
    byte[] salt = new byte[length];
    SECURE_RANDOM.nextBytes(salt);
    return salt;
  }
}
