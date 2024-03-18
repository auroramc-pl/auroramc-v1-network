package pl.auroramc.economy.message;

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

  public MutableMessage missingCurrency = MutableMessage.of(
      "<red>Wprowadzona przez ciebie waluta <dark_gray>(<yellow>{currencyId}<dark_gray>) <red>nie została odnaleziona, upewnij się, czy jest ona poprawna."
  );

  public MutableMessage leaderboardHeader = MutableMessage.of(
      "<gray>Ranking najbogatszych graczy:<newline>"
  );

  public MutableMessage leaderboardFooter = MutableMessage.of(
      "<gray>Twoja pozycja:<newline><yellow>{position}) <gray>{username} <dark_gray>- <white>{currency}{balance}"
  );

  public MutableMessage leaderboardEntry = MutableMessage.of(
      "<dark_gray>{position}) <gray>{username} <dark_gray>- <white>{currency}{balance}"
  );

  public MutableMessage balanceSummaryHeader = MutableMessage.of(
      "<gray>Podsumowanie stanu konta:"
  );

  public MutableMessage balanceSummaryHeaderTargeted = MutableMessage.of(
      "<gray>Podsumowanie stanu konta gracza <white>{username}<gray>:"
  );

  public MutableMessage balanceSummaryEntry = MutableMessage.of(
      "<dark_gray>► <gray>{name} <dark_gray>- <white>{currency}{balance}"
  );

  public MutableMessage noIncomingPayments = MutableMessage.of(
      "<red>Gracz <yellow>{username} <red>nie otrzymał jeszcze żadnych płatności przychodzących."
  );

  public MutableMessage noOutgoingPayments = MutableMessage.of(
      "<red>Gracz <yellow>{username} <red>nie wykonał jeszcze żadnych płatności wychodzących."
  );

  public MutableMessage incomingPaymentsHeader = MutableMessage.of(
      "<gray>Płatności przychodzące dla <white>{username}<dark_gray>:"
  );

  public MutableMessage outgoingPaymentsHeader = MutableMessage.of(
      "<gray>Płatności wychodzące od <white>{username}<dark_gray>:"
  );

  public MutableMessage paymentEntry = MutableMessage.of(
      "<gray>• <dark_gray>{transactionTime} <dark_gray>(<white>{transactionId}<dark_gray>) <dark_gray>(<white>{initiator} <dark_gray>→ <white>{receiver}<dark_gray>) <dark_gray>- <white>{currency}{amount}"
  );

  public MutableMessage transferAmountHasToBeGreaterThanZero = MutableMessage.of(
      "<red>Kwota musi być większa od zera."
  );

  public MutableMessage transferRequiresTarget = MutableMessage.of(
      "<red>Nie możesz przelać pieniędzy samemu sobie."
  );

  public MutableMessage transferMissingBalance = MutableMessage.of(
      "<red>Nie posiadasz wystarczającej ilości pieniędzy, aby wykonać ten przelew."
  );

  public MutableMessage transferSent = MutableMessage.of(
      "<gray>Wysłałeś przelew do <white>{target}<gray>, <gray>z twojego konta zostało odebrane <white>{currency}{amount}<gray>."
  );

  public MutableMessage transferReceived = MutableMessage.of(
      "<gray>Otrzymałeś przelew od <white>{source}<gray>, do twojego konta zostało dodane <white>{currency}{amount}<gray>."
  );

  public MutableMessage transferFailed = MutableMessage.of(
      "<red>Operacja nie została wykonana, gdyż nie udało się odnaleźć waluty z id pasującym do <yellow>{currencyId}<red>."
  );

  public MutableMessage balanceSet = MutableMessage.of(
      "<gray>Saldo gracza <white>{username}<gray> zostało ustawione na <white>{currency}{amount}<gray>."
  );

  public MutableMessage balanceDeposited = MutableMessage.of(
      "<gray>Do konta gracza <white>{username}<gray> zostało dodane <white>{currency}{amount}<gray>."
  );

  public MutableMessage balanceWithdrawn = MutableMessage.of(
      "<gray>Z konta gracza <white>{username}<gray> zostało odebrane <white>{currency}{amount}<gray>."
  );

  public MutableMessage modificationAmountHasToBeGreaterThanZero = MutableMessage.of(
      "<red>Kwota musi być większa od zera."
  );

  public MutableMessage modificationFailed = MutableMessage.of(
      "<red>Operacja nie została wykonana, gdyż nie udało się odnaleźć waluty z id pasującym do <yellow>{currencyId}<red>."
  );
}
