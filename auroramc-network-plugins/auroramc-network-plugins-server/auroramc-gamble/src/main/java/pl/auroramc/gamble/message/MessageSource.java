package pl.auroramc.gamble.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

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
      "<gray>Zakład o stawce <white>{symbol}{stake} <gray>na <white>{prediction} <gray>został utworzony i oczekuje na przeciwnika."
  );

  public MutableMessage stakeWon = MutableMessage.of(
      "<gray>Wygrałeś <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>zakład</hover> <gray>o stawce <white>{symbol}{stake} <gray>mierząc się z <white>{competitor}<gray>."
  );

  public MutableMessage stakeLost = MutableMessage.of(
      "<gray>Przegrałeś <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>zakład</hover> <gray>o stawce <white>{symbol}{stake} <gray>mierząc się z <white>{competitor}<gray>."
  );
}
