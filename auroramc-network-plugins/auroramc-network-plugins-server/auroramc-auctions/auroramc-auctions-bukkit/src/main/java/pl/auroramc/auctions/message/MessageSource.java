package pl.auroramc.auctions.message;

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

  public MutableMessage auctionQueueIsFull = MutableMessage.of(
      "<red>Musisz spróbować wystawić przedmiot później, gdyż aktualnie osiągnięty został limit oczekujących aukcji."
  );

  public MutableMessage requiresHoldingItem = MutableMessage.of(
      "<red>Musisz trzymać przedmiot w ręce, aby móc go wystawić na aukcję."
  );

  public MutableMessage invalidStock = MutableMessage.of(
      "<red>Wprowadzony przez ciebie nakład jest nieprawidłowy."
  );

  public MutableMessage invalidStockBecauseOfMissingItems = MutableMessage.of(
      "<red>Wprowadzony przez ciebie nakład przewyższa posiadane przez ciebie przedmioty."
  );

  public MutableMessage invalidMinimalPrice = MutableMessage.of(
      "<red>Wprowadzona przez ciebie kwota startowa jest nieprawidłowa."
  );

  public MutableMessage invalidMinimalPricePuncture = MutableMessage.of(
      "<red>Wprowadzona przez ciebie kwota przebicia jest nieprawidłowa."
  );

  public MutableMessage auctionSchedule = MutableMessage.of(
      "<gray>Trzymany przez ciebie przedmiot został wystawiony. Aukcja rozpocznie się, gdy nadejdzie jej kolej."
  );

  public MutableMessage offerMissingAuction = MutableMessage.of(
      "<red>Nie możesz złożyć oferty, gdyż w tej chwili nie trwa żadna aukcja."
  );

  public MutableMessage offerSelfAuction = MutableMessage.of(
      "<red>Nie możesz złożyć oferty, gdyż jest to twoja aukcja."
  );

  public MutableMessage offerIsAlreadyHighest = MutableMessage.of(
      "<red>Nie możesz złożyć następnej oferty, gdyż twoja oferta jest w tej chwili największa."
  );

  public MutableMessage offerIsSmallerThanHighestOffer = MutableMessage.of(
      "<red>Nie możesz złożyć oferty, gdyż jest ona mniejsza od aktualnej oferty."
  );

  public MutableMessage offerNotEnoughBalance = MutableMessage.of(
      "<red>Nie posiadasz wystarczająco pieniędzy, aby złożyć tą ofertę."
  );

  public MutableMessage offered = MutableMessage.of(
      "<gray>Złożyłeś ofertę w wysokości <white>{currency}{offer}<gray>."
  );

  public MutableMessage offeringFailed = MutableMessage.of(
      "<red>Wystąpił nieoczekiwany błąd podczas złożenia oferty."
  );

  public MutableMessage auctionIsMissing = MutableMessage.of(
      "<red>W tej chwili nie trwa żadna aukcja."
  );

  public MutableMessage auctionWinningBid = MutableMessage.of(
      "<white>{currency}{offer} <dark_gray>(<white>{trader}<dark_gray>)"
  );

  public MutableMessage auctionSummary = MutableMessage.of(
      """
      <gray>Informacje na temat bieżącej <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>aukcji</hover><dark_gray>:
      <dark_gray>► <gray>Przedmiot: <white>{subject}
      <dark_gray>► <gray>Osoba wystawiająca: <white>{vendor}
      <dark_gray>► <gray>Największa oferta: {highestBid}
      <dark_gray>► <gray>Minimalna kwota startowa: <white>{currency}{minimalPrice}
      <dark_gray>► <gray>Minimalna kwota przebicia: <white>{currency}{minimalPricePuncture}
      """.trim()
  );

  public MutableMessage unknownOffer = MutableMessage.of(
      "<white>Brak"
  );

  public MutableMessage unknownPlayer = MutableMessage.of(
      "<gray>Nieznany"
  );

  public MutableMessage notificationsEnabled = MutableMessage.of(
      "<gray>Włączyłeś wyświetlanie powiadomień dotyczących aukcji."
  );

  public MutableMessage notificationsDisabled = MutableMessage.of(
      "<gray>Wyłączyłeś wyświetlanie powiadomień dotyczących aukcji."
  );

  public MutableMessage auctionNearCompletion = MutableMessage.of(
      "<hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>Aukcja</hover> zakończy się za <white>{period}s<gray>."
  );

  public MutableMessage auctionHasStarted = MutableMessage.of(
      "<gray>Gracz <white>{vendor}</white> rozpoczął <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>aukcję</hover> o przedmiot <white>{subject}</white>. Kwota początkowa wynosi <white>{currency}{minimalPrice}</white>, a minimalna kwota przebicia to <white>{currency}{minimalPricePuncture}</white>."
  );

  public MutableMessage auctionHasCompletedWithoutOffers = MutableMessage.of(
      "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>Aukcja</hover> zakończyła się bez ofert. Przedmiot został zwrócony do <white>{vendor}</white>."
  );

  public MutableMessage auctionHasCompleted = MutableMessage.of(
      "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>Aukcja</hover> gracza <white>{vendor}</white> zakończyła się. Przedmiot <white>{subject}</white> został sprzedany za <white>{currency}{currentOffer}</white> graczowi <white>{currentTrader}</white>."
  );

  public MutableMessage auctionReceivedOffer = MutableMessage.of(
      "<gray>Gracz <white>{current_trader}</white> złożył ofertę <white>{currency}{current_offer}</white> na <hover:show_text:'<gray>Unikalny identyfikator: <white>{unique_id}'>aukcję</hover> o przedmiot <white>{subject}</white>."
  );

  public MutableMessage auctionHasBeenExtended = MutableMessage.of(
      "<gray>Czas trwania <hover:show_text:'<gray>Unikalny identyfikator: <white>{unique_id}'>aukcji</hover> został przedłużony o <white>{offset}</white>."
  );

  public MutableMessage vaultItemReceived = MutableMessage.of(
      "<gray>Do twojego schowka został dodany <white>{subject}<gray>, możesz odebrać go używając <white><click:run_command:/vault>/vault</click><gray>."
  );

  public MutableMessage vaultItemRedeemed = MutableMessage.of(
      "<gray>Odebrałeś <white>{subject} <gray>ze swojego schowka."
  );

  public MutableMessage vaultItemRedeemSuggestion = MutableMessage.of(
      "<gray>Naciśnij aby odebrać ten przedmiot."
  );
}
