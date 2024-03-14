package pl.auroramc.auth.password;

public interface PasswordValidator {

  boolean validatePassword(final String password);
}
