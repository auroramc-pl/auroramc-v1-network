package pl.auroramc.gamble.message;

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

  public MutableMessage missingStakes = MutableMessage.of(
      "<red>W tym momencie nie ma dostępnych zakładów, spróbuj ponownie później."
  );

  public MutableMessage missingStakePage = MutableMessage.of(
      "<red>Wprowadzona przez ciebie strona nie jest dostępna, upewnij się, czy poprawnie ją wprowadziłeś."
  );

  public MutableMessage displayStakeView = MutableMessage.of(
      "<gray>Otworzyłeś podgląd dostępnych zakładów, aby dołączyć do jednego z nich naciśnij na wybrany przez ciebie zakład lewym przyciskiem myszy <dark_gray>(<white>LPM<dark_gray>)<gray>."
  );

  public MutableMessage stakeMissingBalance = MutableMessage.of(
      "<red>Nie posiadasz wystarczających środków aby utworzyć ten zakład."
  );

  public MutableMessage stakeMustBeGreaterThanZero = MutableMessage.of(
      "<red>Stawka musi być większa od zera."
  );

  public MutableMessage stakeCreated = MutableMessage.of(
      "<gray>Zakład o stawce <white>{currency}{stake} <gray>na <white>{prediction} <gray>został utworzony i oczekuje na przeciwnika."
  );

  public MutableMessage stakeWon = MutableMessage.of(
      "<gray>Wygrałeś <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>zakład</hover> <gray>o stawce <white>{currency}{stake} <gray>mierząc się z <white>{competitor}<gray>."
  );

  public MutableMessage stakeLost = MutableMessage.of(
      "<gray>Przegrałeś <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>zakład</hover> <gray>o stawce <white>{currency}{stake} <gray>mierząc się z <white>{competitor}<gray>."
  );

  public MutableMessage stakeFinalizationSelf = MutableMessage.of(
      "<red>Nie możesz dołączyć do własnego zakładu."
  );

  public MutableMessage stakeFinalizationMissingBalance = MutableMessage.of(
      "<red>Nie posiadasz wystarczających środków aby dołączyć do tego zakładu."
  );

  public MutableMessage stakesTitle = MutableMessage.of(
      "Oczekujące zakłady"
  );

  public MutableMessage stakeName = MutableMessage.of(
      "<gray>Zakład <dark_gray>(<white>{gamble}<dark_gray>)"
  );

  public MutableMessage stakeBrief = MutableMessage.empty()
      .append(
          MutableMessage.of(
              "<gray>Gracz: <white>{initiator}"
          )
      )
      .append(
          MutableMessage.of(
              "<gray>Wybór: <white>{prediction}"
          )
      )
      .append(
          MutableMessage.of(
              "<gray>Stawka: <white>{currency}{stake}"
          )
      )
      .append(
          MutableMessage.of(
              "<gray>Naciśnij aby dołączyć do zakładu."
          )
      );

  public MutableMessage navigateForward = MutableMessage.of(
      "<gray>Następna strona"
  );

  public MutableMessage navigateForwardSuggestion = MutableMessage.of(
      "<gray>Naciśnij aby przejść do następnej strony."
  );

  public MutableMessage navigateBackward = MutableMessage.of(
      "<gray>Poprzednia strona"
  );

  public MutableMessage navigateBackwardSuggestion = MutableMessage.of(
      "<gray>Naciśnij aby przejść do poprzedniej strony."
  );
}
