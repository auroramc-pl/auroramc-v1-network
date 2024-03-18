package pl.auroramc.cheque.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public MutableMessage specifiedPlayerIsUnknown = MutableMessage.of(
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
      "<gray>Wystawiłeś czek o wartości <white>{currency}{amount}<gray>."
  );

  public MutableMessage chequeCouldNotBeCreatedBecauseOfDigits = MutableMessage.of(
      "<red>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż maksymalnie może ona posiadać {maximumIntegralLength} liczb przed przecinkiem oraz {maximumFractionLength} liczb po przecinku."
  );

  public MutableMessage chequeCouldNotBeCreatedBecauseOfAmount = MutableMessage.of(
      "<red>Wprowadzona przez ciebie kwota czeku jest niepoprawna, gdyż musi ona wynosić co najmniej {currency}{minimumChequeWorth} oraz co najwyżej {currency}{maximumChequeWorth}."
  );

  public MutableMessage chequeCouldNotBeCreatedBecauseOfMoney = MutableMessage.of(
      "<red>Nie posiadasz wystarczających środków aby wystawić czek o tej wartości."
  );

  public MutableMessage chequeFinalized = MutableMessage.of(
      "<gray>Sfinalizowałeś czek na kwotę <white>{currency}{amount} <gray>wystawiony przez <white>{issuer}<gray>."
  );

  public MutableMessage titleOfCheque = MutableMessage.of(
      "<gray>Czek <dark_gray>(<white>{currency}{amount}<dark_gray>)"
  );

  public MutableMessage linesOfCheque = MutableMessage.of(
      "<gray>Wystawiający: <white>{issuer}"
  );
}
