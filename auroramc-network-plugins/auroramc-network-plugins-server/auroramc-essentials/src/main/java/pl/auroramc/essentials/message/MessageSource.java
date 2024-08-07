package pl.auroramc.essentials.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.messages.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage titleOfSummary =
      MutableMessage.of(
          """
      <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Serwer składa się w <#f4a9ba><percentage>% <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>z autorskich wtyczek
      stworzonych dedykowanie dla naszego serwera, wszystkie z nich nie są dostępne
      do uzyskania dla osób trzecich. Niżej dostępna lista wtyczek zawiera tylko
      autorskie wtyczki, w celu zapobiegnięcia próbom znalezienia oraz
      wykorzystania błędów związanych z resztą wtyczek.

      <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Lista wykorzystanych wtyczek:\s
      """
              .trim());

  public MutableMessage entryOfSummary =
      MutableMessage.of(
          "<#f4a9ba><pluginName><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><separator>");

  public MutableMessage unknownCommand =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie komenda nie została znaleziona.");

  public MutableMessage unknownCommandWithPotentialSuggestion =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie komenda nie została znaleziona. Czy nie chodziło Ci może przypadkiem o <gradient:#ffad33:#ff8052:#ffdb57:#ff8052:#ffdb57:#ffad33>/<suggestion><gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>?");

  public MutableMessage potentialSuggestionHover =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Naciśnij, aby użyć sugestii dotyczącej poprawnej komendy.");
}
