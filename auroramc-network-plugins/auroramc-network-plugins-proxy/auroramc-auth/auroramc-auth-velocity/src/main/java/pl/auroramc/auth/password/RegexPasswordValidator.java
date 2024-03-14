package pl.auroramc.auth.password;

import java.util.regex.Pattern;

class RegexPasswordValidator implements PasswordValidator {

  private final Pattern passwordPattern;

  RegexPasswordValidator(final String rawPasswordPattern) {
    this.passwordPattern = Pattern.compile(rawPasswordPattern);
  }

  @Override
  public boolean validatePassword(final String password) {
    return passwordPattern.matcher(password).matches();
  }
}
