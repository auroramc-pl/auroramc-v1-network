package pl.auroramc.auctions.auction;

import eu.okaeri.configs.OkaeriConfig;
import pl.auroramc.messages.message.MutableMessage;

public class AuctionMessageSource extends OkaeriConfig {

  public MutableMessage auctionQueueIsFull =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Musisz spróbować wystawić przedmiot później, gdyż aktualnie osiągnięty został limit oczekujących aukcji.");

  public MutableMessage requiresHoldingItem =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Musisz trzymać przedmiot w ręce, aby móc go wystawić na aukcję.");

  public MutableMessage invalidStock =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzony przez ciebie nakład jest nieprawidłowy.");

  public MutableMessage invalidStockBecauseOfMissingItems =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzony przez ciebie nakład przewyższa posiadane przez ciebie przedmioty.");

  public MutableMessage invalidMinimalPrice =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie kwota startowa jest nieprawidłowa.");

  public MutableMessage invalidMinimalPricePuncture =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wprowadzona przez ciebie kwota przebicia jest nieprawidłowa.");

  public MutableMessage auctionSchedule =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Trzymany przez ciebie przedmiot został wystawiony. Aukcja rozpocznie się, gdy nadejdzie jej kolej.");

  public MutableMessage offerMissingAuction =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz złożyć oferty, gdyż w tej chwili nie trwa żadna aukcja.");

  public MutableMessage offerSelfAuction =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz złożyć oferty, gdyż jest to twoja aukcja.");

  public MutableMessage offerIsAlreadyHighest =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz złożyć następnej oferty, gdyż twoja oferta jest w tej chwili największa.");

  public MutableMessage offerIsSmallerThanHighestOffer =
      MutableMessage.of(
          "<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie możesz złożyć oferty, gdyż jest ona mniejsza od aktualnej oferty.");

  public MutableMessage offerNotEnoughBalance =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Nie posiadasz wystarczająco pieniędzy, aby złożyć tą ofertę.");

  public MutableMessage offered =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Złożyłeś ofertę w wysokości <#f4a9ba><context.currency.@symbol><context.amount><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage offeringFailed =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>Wystąpił nieoczekiwany błąd podczas złożenia oferty.");

  public MutableMessage auctionIsMissing =
      MutableMessage.of("<gradient:#b51c1c:#d33131:#c72929:#d33131:#b51c1c>W tej chwili nie trwa żadna aukcja.");

  public MutableMessage auctionWinningBid =
      MutableMessage.of(
          "<#f4a9ba><context.currency.@symbol><context.amount> <#7c5058>(<#f4a9ba><auction.trader><#7c5058>)");

  public MutableMessage auctionSummary =
      MutableMessage.of(
          """
          <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Informacje na temat bieżącej <hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>aukcji</hover><#7c5058>:
          <#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Przedmiot: <#7c5058>x<auction.@resolvedSubject.@amount></dark_gray> <#f4a9ba><auction.@resolvedSubject>
          <#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Osoba wystawiająca: <#f4a9ba><auction.vendor>
          <#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Największa oferta: <#f4a9ba><auction.offer>
          <#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Minimalna kwota startowa: <#f4a9ba><currency.@symbol><auction.@minimalPrice>
          <#7c5058>► <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Minimalna kwota przebicia: <#f4a9ba><currency.@symbol><auction.@minimalPricePuncture>
          """
              .trim());

  public MutableMessage unknownOffer = MutableMessage.of("<#f4a9ba>Brak");

  public MutableMessage unknownPlayer = MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Nieznany");

  public MutableMessage notificationsEnabled =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Włączyłeś wyświetlanie powiadomień dotyczących aukcji.");

  public MutableMessage notificationsDisabled =
      MutableMessage.of("<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Wyłączyłeś wyświetlanie powiadomień dotyczących aukcji.");

  public MutableMessage auctionNearCompletion =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>Aukcja</hover> <gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>zakończy się za <#f4a9ba><duration><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>.");

  public MutableMessage auctionHasStarted =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Gracz <#f4a9ba><auction.vendor></white> rozpoczął <hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>aukcję</hover> o przedmiot <#7c5058>x<auction.@resolvedSubject.@amount></dark_gray> <#f4a9ba><auction.@resolvedSubject></white><gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>. Kwota początkowa wynosi <#f4a9ba><currency.@symbol><auction.@minimalPrice></white>, a minimalna kwota przebicia to <#f4a9ba><currency.@symbol><auction.@minimalPricePuncture></white>.");

  public MutableMessage auctionHasCompletedWithoutOffers =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>Aukcja</hover> zakończyła się bez ofert. Przedmiot został zwrócony do <#f4a9ba><auction.vendor></white>.");

  public MutableMessage auctionHasCompleted =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b><hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>Aukcja</hover> gracza <#f4a9ba><auction.vendor></white> zakończyła się. Przedmiot <#7c5058>x<auction.@resolvedSubject.@amount></dark_gray> <#f4a9ba><auction.@resolvedSubject></white> został sprzedany za <#f4a9ba><currency.@symbol><auction.@currentOffer></white> graczowi <#f4a9ba><auction.trader></white>.");

  public MutableMessage auctionReceivedOffer =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Gracz <#f4a9ba><auction.trader></white> złożył ofertę <#f4a9ba><currency.@symbol><auction.@currentOffer></white> na <hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>aukcję</hover> o przedmiot <#7c5058>x<auction.@resolvedSubject.@amount></dark_gray> <#f4a9ba><auction.@resolvedSubject></hover></white>.");

  public MutableMessage auctionHasBeenExtended =
      MutableMessage.of(
          "<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Czas trwania <hover:show_text:'<gradient:#c95e7b:#ed7d95:#ed7d95:#b55e7b>Unikalny identyfikator: <#f4a9ba><auction.@auctionUniqueId>'>aukcji</hover> został przedłużony o <#f4a9ba><duration></white>.");
}
