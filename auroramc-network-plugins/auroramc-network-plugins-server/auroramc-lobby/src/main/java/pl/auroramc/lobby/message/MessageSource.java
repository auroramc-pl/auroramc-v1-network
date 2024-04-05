package pl.auroramc.lobby.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage commandOnCooldown =
      MutableMessage.of(
          "<red>Musisz odczekać jeszcze <yellow>{duration}<red>, zanim ponownie użyjesz tej komendy.");

  public MutableMessage availableSchematicsSuggestion =
      MutableMessage.of("<gray>Dostępne schematy: <white>{schematics}");

  public MutableMessage executionOfCommandIsNotPermitted =
      MutableMessage.of("<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy.");

  public MutableMessage executionFromConsoleIsUnsupported =
      MutableMessage.of("<red>Nie możesz użyć tej konsoli z poziomu konsoli.");

  public MutableMessage teleportedIntoSpawn =
      MutableMessage.of("<gray>Zostałeś przeteleportowany na spawn.");

  public MutableMessage lobbyClarification =
      MutableMessage.of(
          """
          <gray>Znajdujesz się w lobby, które przeznaczone jest do uwierzytelnienia,
          po poprawnym zakończeniu tego procesu, zostaniesz przeniesiony na serwer docelowy.
          Powodzenia!
          """);

  public MutableMessage teleportedFromVoid =
      MutableMessage.of(
          "<gray>Zostałeś przeteleportowany na spawn, z powodu twojej obecności w otchłani.");
}
