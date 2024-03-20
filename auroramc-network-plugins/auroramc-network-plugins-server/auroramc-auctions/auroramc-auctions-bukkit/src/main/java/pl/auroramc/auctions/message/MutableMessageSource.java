package pl.auroramc.auctions.message;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Exclude;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;

public class MutableMessageSource extends OkaeriConfig {

  public static final @Exclude String MESSAGE_SOURCE_FILE_NAME = "messages.yml";

  public DeliverableMutableMessage availableSchematicsSuggestion =
      DeliverableMutableMessage.of(
          MutableMessage.of(
              "<red>Poprawne użycie: <yellow><newline>{schematics}"
          )
      );

  public DeliverableMutableMessage executionOfCommandIsNotPermitted = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie posiadasz wystarczających uprawnień aby użyć tej komendy."
      )
  );

  public DeliverableMutableMessage executionFromConsoleIsUnsupported = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie możesz użyć tej konsoli z poziomu konsoli."
      )
  );

  public DeliverableMutableMessage auctionQueueIsFull = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Musisz spróbować wystawić przedmiot później, gdyż aktualnie osiągnięty został limit oczekujących aukcji."
      )
  );

  public DeliverableMutableMessage requiresHoldingItem = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Musisz trzymać przedmiot w ręce, aby móc go wystawić na aukcję."
      )
  );

  public DeliverableMutableMessage invalidStock = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Wprowadzony przez ciebie nakład jest nieprawidłowy."
      )
  );

  public DeliverableMutableMessage invalidStockBecauseOfMissingItems = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Wprowadzony przez ciebie nakład przewyższa posiadane przez ciebie przedmioty."
      )
  );

  public DeliverableMutableMessage invalidMinimalPrice = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie kwota startowa jest nieprawidłowa."
      )
  );

  public DeliverableMutableMessage invalidMinimalPricePuncture = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Wprowadzona przez ciebie kwota przebicia jest nieprawidłowa."
      )
  );

  public DeliverableMutableMessage auctionSchedule = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Trzymany przez ciebie przedmiot został wystawiony. Aukcja rozpocznie się, gdy nadejdzie jej kolej."
      )
  );

  public DeliverableMutableMessage offerMissingAuction = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie możesz złożyć oferty, gdyż w tej chwili nie trwa żadna aukcja."
      )
  );

  public DeliverableMutableMessage offerSelfAuction = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie możesz złożyć oferty, gdyż jest to twoja aukcja."
      )
  );

  public DeliverableMutableMessage offerIsAlreadyHighest = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie możesz złożyć następnej oferty, gdyż twoja oferta jest w tej chwili największa."
      )
  );

  public DeliverableMutableMessage offerIsSmallerThanHighestOffer = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie możesz złożyć oferty, gdyż jest ona mniejsza od aktualnej oferty."
      )
  );

  public DeliverableMutableMessage offerNotEnoughBalance = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Nie posiadasz wystarczająco pieniędzy, aby złożyć tą ofertę."
      )
  );

  public DeliverableMutableMessage offered = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Złożyłeś ofertę w wysokości <white>{currency}{offer}<gray>."
      )
  );

  public DeliverableMutableMessage offeringFailed = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>Wystąpił nieoczekiwany błąd podczas złożenia oferty."
      )
  );

  public DeliverableMutableMessage auctionIsMissing = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<red>W tej chwili nie trwa żadna aukcja."
      )
  );

  public DeliverableMutableMessage auctionWinningBid = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<white>{currency}{offer} <dark_gray>(<white>{trader}<dark_gray>)"
      )
  );

  public DeliverableMutableMessage auctionSummary = DeliverableMutableMessage.of(
      MutableMessage.of(
          """
          <gray>Informacje na temat bieżącej <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>aukcji</hover><dark_gray>:
          <dark_gray>► <gray>Przedmiot: <white>{subject}</hover>
          <dark_gray>► <gray>Osoba wystawiająca: <white>{vendor}
          <dark_gray>► <gray>Największa oferta: <white>{highestBid}
          <dark_gray>► <gray>Minimalna kwota startowa: <white>{currency}{minimalPrice}
          <dark_gray>► <gray>Minimalna kwota przebicia: <white>{currency}{minimalPricePuncture}
          """.trim()
      )
  );

  public DeliverableMutableMessage unknownOffer = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<white>Brak"
      )
  );

  public DeliverableMutableMessage unknownPlayer = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Nieznany"
      )
  );

  public DeliverableMutableMessage notificationsEnabled = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Włączyłeś wyświetlanie powiadomień dotyczących aukcji."
      )
  );

  public DeliverableMutableMessage notificationsDisabled = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Wyłączyłeś wyświetlanie powiadomień dotyczących aukcji."
      )
  );

  public DeliverableMutableMessage auctionNearCompletion = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>Aukcja</hover> <gray>zakończy się za <white>{period}s<gray>."
      )
  );

  public DeliverableMutableMessage auctionHasStarted = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Gracz <white>{vendor}</white> rozpoczął <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>aukcję</hover> o przedmiot <white>{subject}</hover></white><gray>. Kwota początkowa wynosi <white>{currency}{minimalPrice}</white>, a minimalna kwota przebicia to <white>{currency}{minimalPricePuncture}</white>."
      )
  );

  public DeliverableMutableMessage auctionHasCompletedWithoutOffers = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>Aukcja</hover> zakończyła się bez ofert. Przedmiot został zwrócony do <white>{vendor}</white>."
      )
  );

  public DeliverableMutableMessage auctionHasCompleted = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>Aukcja</hover> gracza <white>{vendor}</white> zakończyła się. Przedmiot <white>{subject}</hover></white> został sprzedany za <white>{currency}{currentOffer}</white> graczowi <white>{currentTrader}</white>."
      )
  );

  public DeliverableMutableMessage auctionReceivedOffer = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Gracz <white>{currentTrader}</white> złożył ofertę <white>{currency}{currentOffer}</white> na <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>aukcję</hover> o przedmiot <white>{subject}</hover></white>."
      )
  );

  public DeliverableMutableMessage auctionHasBeenExtended = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Czas trwania <hover:show_text:'<gray>Unikalny identyfikator: <white>{uniqueId}'>aukcji</hover> został przedłużony o <white>{offset}</white>."
      )
  );

  public DeliverableMutableMessage vaultItemReceived = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Do twojego schowka został dodany <white>{subject}<gray>, możesz odebrać go używając <white><click:run_command:/vault>/vault</click><gray>."
      )
  );

  public DeliverableMutableMessage vaultItemRedeemed = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Odebrałeś <white>{subject} <gray>ze swojego schowka."
      )
  );

  public DeliverableMutableMessage vaultItemRedeemSuggestion = DeliverableMutableMessage.of(
      MutableMessage.of(
          "<gray>Naciśnij aby odebrać ten przedmiot."
      )
  );
}
