package pl.auroramc.auth.hash;

public interface HashingStrategy {

  String hashPassword(final String plainPassword);

  boolean verifyPassword(final String plainPassword, final CharSequence hashedPassword);
}
