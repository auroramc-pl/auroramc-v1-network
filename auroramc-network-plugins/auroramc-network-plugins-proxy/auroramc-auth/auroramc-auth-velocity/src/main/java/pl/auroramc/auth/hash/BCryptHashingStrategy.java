package pl.auroramc.auth.hash;

import static java.nio.charset.StandardCharsets.UTF_8;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Hasher;
import at.favre.lib.crypto.bcrypt.BCrypt.Verifyer;
import pl.auroramc.auth.hash.salt.SaltGenerator;

class BCryptHashingStrategy implements HashingStrategy {

  private static final int BCRYPT_HASHING_ROUNDS = 10;
  private static final int PASSWORD_SALT_LENGTH = 16;
  private static final Hasher PASSWORD_HASHER = BCrypt.withDefaults();
  private static final Verifyer PASSWORD_VERIFYER = BCrypt.verifyer();
  private final SaltGenerator saltGenerator;

  BCryptHashingStrategy(final SaltGenerator saltGenerator) {
    this.saltGenerator = saltGenerator;
  }

  @Override
  public String hashPassword(final String plainPassword) {
    final byte[] password = plainPassword.getBytes(UTF_8);
    final byte[] passwordSalt = saltGenerator.generateSalt(PASSWORD_SALT_LENGTH);
    final byte[] encodedPassword =
        PASSWORD_HASHER.hash(BCRYPT_HASHING_ROUNDS, passwordSalt, password);
    return new String(encodedPassword, UTF_8);
  }

  @Override
  public boolean verifyPassword(final String plainPassword, final CharSequence hashedPassword) {
    return PASSWORD_VERIFYER.verify(plainPassword.toCharArray(), hashedPassword).verified;
  }
}
