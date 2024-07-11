package pl.auroramc.auctions.auction;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class AuctionMessageSource extends OkaeriConfig {

  public MutableMessage auctionQueueIsFull =
      MutableMessage.of(
          "<red>Musisz spróbować wystawić przedmiot później, gdyż aktualnie osiągnięty został limit oczekujących aukcji.");

  public MutableMessage requiresHoldingItem =
      MutableMessage.of("<red>Musisz trzymać przedmiot w ręce, aby móc go wystawić na aukcję.");

  public MutableMessage invalidStock =
      MutableMessage.of("<red>Wprowadzony przez ciebie nakład jest nieprawidłowy.");

  public MutableMessage invalidStockBecauseOfMissingItems =
      MutableMessage.of(
          "<red>Wprowadzony przez ciebie nakład przewyższa posiadane przez ciebie przedmioty.");

  public MutableMessage invalidMinimalPrice =
      MutableMessage.of("<red>Wprowadzona przez ciebie kwota startowa jest nieprawidłowa.");

  public MutableMessage invalidMinimalPricePuncture =
      MutableMessage.of("<red>Wprowadzona przez ciebie kwota przebicia jest nieprawidłowa.");

  public MutableMessage auctionSchedule =
      MutableMessage.of(
          "<gray>Trzymany przez ciebie przedmiot został wystawiony. Aukcja rozpocznie się, gdy nadejdzie jej kolej.");

  public MutableMessage offerMissingAuction =
      MutableMessage.of("<red>Nie możesz złożyć oferty, gdyż w tej chwili nie trwa żadna aukcja.");

  public MutableMessage offerSelfAuction =
      MutableMessage.of("<red>Nie możesz złożyć oferty, gdyż jest to twoja aukcja.");

  public MutableMessage offerIsAlreadyHighest =
      MutableMessage.of(
          "<red>Nie możesz złożyć następnej oferty, gdyż twoja oferta jest w tej chwili największa.");

  public MutableMessage offerIsSmallerThanHighestOffer =
      MutableMessage.of(
          "<red>Nie możesz złożyć oferty, gdyż jest ona mniejsza od aktualnej oferty.");

  public MutableMessage offerNotEnoughBalance =
      MutableMessage.of("<red>Nie posiadasz wystarczająco pieniędzy, aby złożyć tą ofertę.");

  public MutableMessage offered =
      MutableMessage.of(
          "<gray>Złożyłeś ofertę w wysokości <white><context.currency.@symbol><context.amount><gray>.");

  public MutableMessage offeringFailed =
      MutableMessage.of("<red>Wystąpił nieoczekiwany błąd podczas złożenia oferty.");

  public MutableMessage auctionIsMissing =
      MutableMessage.of("<red>W tej chwili nie trwa żadna aukcja.");

  public MutableMessage auctionWinningBid =
      MutableMessage.of(
          "<white><context.currency.@symbol><context.amount> <dark_gray>(<white><auction.trader><dark_gray>)");

  public MutableMessage auctionSummary =
      MutableMessage.of(
          """
          <gray>Informacje na temat bieżącej <hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>aukcji</hover><dark_gray>:
          <dark_gray>► <gray>Przedmiot: <dark_gray>x<auction.@resolvedSubject.@amount></dark_gray> <white><auction.@resolvedSubject>
          <dark_gray>► <gray>Osoba wystawiająca: <white><auction.vendor>
          <dark_gray>► <gray>Największa oferta: <white><auction.offer>
          <dark_gray>► <gray>Minimalna kwota startowa: <white><currency.@symbol><auction.@minimalPrice>
          <dark_gray>► <gray>Minimalna kwota przebicia: <white><currency.@symbol><auction.@minimalPricePuncture>
          """
              .trim());

  public MutableMessage unknownOffer = MutableMessage.of("<white>Brak");

  public MutableMessage unknownPlayer = MutableMessage.of("<gray>Nieznany");

  public MutableMessage notificationsEnabled =
      MutableMessage.of("<gray>Włączyłeś wyświetlanie powiadomień dotyczących aukcji.");

  public MutableMessage notificationsDisabled =
      MutableMessage.of("<gray>Wyłączyłeś wyświetlanie powiadomień dotyczących aukcji.");

  public MutableMessage auctionNearCompletion =
      MutableMessage.of(
          "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>Aukcja</hover> <gray>zakończy się za <white><duration><gray>.");

  public MutableMessage auctionHasStarted =
      MutableMessage.of(
          "<gray>Gracz <white><auction.vendor></white> rozpoczął <hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>aukcję</hover> o przedmiot <dark_gray>x<auction.@resolvedSubject.@amount></dark_gray> <white><auction.@resolvedSubject></white><gray>. Kwota początkowa wynosi <white><currency.@symbol><auction.@minimalPrice></white>, a minimalna kwota przebicia to <white><currency.@symbol><auction.@minimalPricePuncture></white>.");

  public MutableMessage auctionHasCompletedWithoutOffers =
      MutableMessage.of(
          "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>Aukcja</hover> zakończyła się bez ofert. Przedmiot został zwrócony do <white><auction.vendor></white>.");

  public MutableMessage auctionHasCompleted =
      MutableMessage.of(
          "<gray><hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>Aukcja</hover> gracza <white><auction.vendor></white> zakończyła się. Przedmiot <dark_gray>x<auction.@resolvedSubject.@amount></dark_gray> <white><auction.@resolvedSubject></white> został sprzedany za <white><currency.@symbol><auction.@currentOffer></white> graczowi <white><auction.trader></white>.");

  public MutableMessage auctionReceivedOffer =
      MutableMessage.of(
          "<gray>Gracz <white><auction.trader></white> złożył ofertę <white><currency.@symbol><auction.@currentOffer></white> na <hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>aukcję</hover> o przedmiot <dark_gray>x<auction.@resolvedSubject.@amount></dark_gray> <white><auction.@resolvedSubject></hover></white>.");

  public MutableMessage auctionHasBeenExtended =
      MutableMessage.of(
          "<gray>Czas trwania <hover:show_text:'<gray>Unikalny identyfikator: <white><auction.@auctionUniqueId>'>aukcji</hover> został przedłużony o <white><duration></white>.");
}
