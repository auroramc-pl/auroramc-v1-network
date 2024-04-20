package pl.auroramc.essentials.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage titleOfSummary =
      MutableMessage.of(
              """
      <gray>Serwer składa się w <white>{percentage}% <gray>z autorskich wtyczek
      stworzonych dedykowanie dla naszego serwera, wszystkie z nich nie są dostępne
      do uzyskania dla osób trzecich. Niżej dostępna lista wtyczek zawiera tylko
      autorskie wtyczki, w celu zapobiegnięcia próbom znalezienia oraz
      wykorzystania błędów związanych z resztą wtyczek.

      <gray>Lista wykorzystanych wtyczek:\s
      """
              .trim());

  public MutableMessage entryOfSummary = MutableMessage.of("<white>{pluginName}<gray>{separator}");

  public MutableMessage unknownCommand =
      MutableMessage.of("<red>Wprowadzona przez ciebie komenda nie została znaleziona.");

  public MutableMessage unknownCommandWithPotentialSuggestion =
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie komenda nie została znaleziona. Czy nie chodziło Ci może przypadkiem o <yellow>/{suggestion}<red>?");

  public MutableMessage potentialSuggestionHover =
      MutableMessage.of("<gray>Naciśnij, aby użyć sugestii dotyczącej poprawnej komendy.");
}
