package pl.auroramc.dailyrewards.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage availableSchematicsSuggestion =
      MutableMessage.of("<red>Poprawne użycie: <yellow><newline>{schematics}");

  public MutableMessage executionOfCommandIsNotPermitted =
      MutableMessage.of("<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy.");

  public MutableMessage executionFromConsoleIsUnsupported =
      MutableMessage.of("<red>Nie możesz użyć tej konsoli z poziomu konsoli.");

  public MutableMessage specifiedPlayerIsUnknown =
      MutableMessage.of("<red>Wskazany przez ciebie gracz nie istnieje, lub jest Offline.");

  public MutableMessage belowName =
      MutableMessage.of("<dark_gray>[ <gradient:white:gray>{playtime} <dark_gray>]");

  public MutableMessage visitDailySummary =
      MutableMessage.of("<gray>Wykaz sesji z dnia <white>{period}<gray>:");

  public MutableMessage visitRangeSummary =
      MutableMessage.of("<gray>Wykaz sesji z okresu <white>{from} <gray>- <white>{to}<gray>:");

  public MutableMessage visitEntry =
      MutableMessage.of(
          "<dark_gray>► (<white>{sessionStart} <dark_gray>- <white>{sessionDitch}<dark_gray>) <dark_gray>- <gray>{playtime}");

  public MutableMessage noVisits = MutableMessage.of("<gray>Brak sesji w podanym okresie.");
}
