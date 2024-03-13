package pl.auroramc.cheque.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage specifiedPlayerIsOffline = MutableMessage.of(
      "<red>Gracz o wskazanej przez ciebie nazwie jest Offline."
  );

  public MutableMessage availableSchematicsSuggestion = MutableMessage.of(
      "<red>Poprawne użycie: <yellow><newline>{schematics}"
  );

  public MutableMessage executionOfCommandIsNotPermitted = MutableMessage.of(
      "<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."
  );

  public MutableMessage executionFromConsoleIsUnsupported = MutableMessage.of(
      "<red>Nie możesz użyć tej konsoli z poziomu konsoli."
  );

  public MutableMessage chequeIssued = MutableMessage.of(
      "<gray>Wystawiłeś czek o wartości <white>{symbol}{amount}<gray>."
  );

  public MutableMessage chequeCouldNotBeCreatedBecauseOfDigits = MutableMessage.of(
      "<red>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż maksymalnie może ona posiadać {maximum-integral-length} liczb przed przecinkiem oraz {maximum-fraction-length} liczb po przecinku."
  );

  public MutableMessage chequeCouldNotBeCreatedBecauseOfAmount = MutableMessage.of(
      "<red>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż musi ona wynosić co najmniej {symbol}{minimum-cheque-worth} oraz co najwyżej {symbol}{maximum-cheque-worth}."
  );

  public MutableMessage chequeCouldNotBeCreatedBecauseOfMoney = MutableMessage.of(
      "<red>Nie posiadasz wystarczających środków aby wystawić czek o tej wartości."
  );

  public MutableMessage chequeFinalized = MutableMessage.of(
      "<gray>Sfinalizowałeś czek na kwotę <white>{symbol}{amount} <gray>wystawiony przez <white>{issuer}<gray>."
  );

  public MutableMessage titleOfCheque = MutableMessage.of(
      "<gray>Czek <dark_gray>(<white>{symbol}{amount}<dark_gray>)"
  );

  public MutableMessage linesOfCheque = MutableMessage.of(
      "<gray>Wystawiający: <white>{issuer}"
  );
}
