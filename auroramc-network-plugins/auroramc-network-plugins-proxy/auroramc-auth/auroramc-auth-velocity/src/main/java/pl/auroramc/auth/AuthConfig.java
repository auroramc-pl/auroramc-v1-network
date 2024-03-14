package pl.auroramc.auth;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import java.util.List;
import pl.auroramc.auth.mail.MailConfig;

public class AuthConfig extends OkaeriConfig {

  static final @Exclude String AUTH_CONFIG_FILE_NAME = "config.yml";

  public String destinedServerId = "skyblock_legacy";

  public String awaitingServerId = "lobby";

  public String usernamePattern = "^[a-zA-Z0-9_]{3,16}$";

  public String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,32}$";

  public String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

  public List<String> defaultCommands = List.of(
      "login", "l", "zaloguj", "register", "reg", "rejestracja", "zarejestruj", "recovery", "recover", "odzyskaj");

  public MailConfig mailConfig = new MailConfig();
}
