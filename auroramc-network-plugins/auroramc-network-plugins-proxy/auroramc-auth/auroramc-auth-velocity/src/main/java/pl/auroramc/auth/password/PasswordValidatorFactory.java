package pl.auroramc.auth.password;

public final class PasswordValidatorFactory {

  private PasswordValidatorFactory() {

  }

  public static PasswordValidator getPasswordValidator(final String rawPasswordPattern) {
    return new RegexPasswordValidator(rawPasswordPattern);
  }
}
