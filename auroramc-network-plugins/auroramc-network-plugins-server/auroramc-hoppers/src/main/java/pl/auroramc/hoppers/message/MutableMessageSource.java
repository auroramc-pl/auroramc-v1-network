package pl.auroramc.hoppers.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage availableSchematicsSuggestion = MutableMessage.of(
      "<red>Poprawne użycie: <yellow><newline>{schematics}"
  );

  public MutableMessage executionOfCommandIsNotPermitted = MutableMessage.of(
      "<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."
  );

  public MutableMessage executionFromConsoleIsUnsupported = MutableMessage.of(
      "<red>Nie możesz użyć tej konsoli z poziomu konsoli."
  );

  public MutableMessage specifiedPlayerIsUnknown = MutableMessage.of(
      "<red>Wskazany przez ciebie gracz nie istnieje, lub jest Offline."
  );

  public MutableMessage hopperHasBeenGiven = MutableMessage.of(
      "<gray>Do twojego ekwipunku został dodany lejek przenoszący <white><quantity> <gray>przedmiotów."
  );

  public MutableMessage hopperDisplayName = MutableMessage.of(
      "<gray>Hopper <dark_gray>(<white><quantity><dark_gray>)"
  );
}