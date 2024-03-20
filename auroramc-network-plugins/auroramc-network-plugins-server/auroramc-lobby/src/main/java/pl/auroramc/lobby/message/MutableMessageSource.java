package pl.auroramc.lobby.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public DeliverableMutableMessage availableSchematicsSuggestion =
      DeliverableMutableMessage.of(
          MutableMessage.of("<gray>Dostępne schematy: <white>{schematics}"));

  public DeliverableMutableMessage executionOfCommandIsNotPermitted =
      DeliverableMutableMessage.of(
          MutableMessage.of("<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."));

  public DeliverableMutableMessage executionFromConsoleIsUnsupported =
      DeliverableMutableMessage.of(
          MutableMessage.of("<red>Nie możesz użyć tej konsoli z poziomu konsoli."));

  public DeliverableMutableMessage teleportedIntoSpawn =
      DeliverableMutableMessage.of(MutableMessage.of("<gray>Zostałeś przeteleportowany na spawn."));

  public DeliverableMutableMessage lobbyClarification =
      DeliverableMutableMessage.of(
          MutableMessage.of(
              """
          <gray>Znajdujesz się w lobby, które przeznaczone jest do uwierzytelnienia,
          po poprawnym zakończeniu tego procesu, zostaniesz przeniesiony na serwer docelowy.
          Powodzenia!
          """));

  public DeliverableMutableMessage teleportedFromVoid =
      DeliverableMutableMessage.of(
          MutableMessage.of(
              "<gray>Zostałeś przeteleportowany na spawn, z powodu twojej obecności w otchłani."));
}
