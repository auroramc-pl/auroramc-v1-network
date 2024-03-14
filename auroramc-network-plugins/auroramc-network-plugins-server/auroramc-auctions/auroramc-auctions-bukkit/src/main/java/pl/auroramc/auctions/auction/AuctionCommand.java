package pl.auroramc.auctions.auction;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionSummaryResolvers;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;
import pl.auroramc.auctions.auction.event.AuctionBidEvent;
import pl.auroramc.auctions.message.viewer.MessageViewer;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;
import pl.auroramc.commons.event.publisher.BukkitEventPublisher;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

@Permission("auroramc.auctions.auction")
@Route(name = "auction")
public class AuctionCommand {

  private final MessageViewerFacade messageViewerFacade;
  private final EconomyFacade economyFacade;
  private final Currency fundsCurrency;
  private final AuctionController auctionController;
  private final BukkitEventPublisher eventPublisher;

  public AuctionCommand(
      final MessageViewerFacade messageViewerFacade,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final AuctionController auctionController,
      final BukkitEventPublisher eventPublisher
  ) {
    this.messageViewerFacade = messageViewerFacade;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
    this.auctionController = auctionController;
    this.eventPublisher = eventPublisher;
  }

  @Permission("auroramc.auctions.auction.schedule")
  @Execute(route = "schedule", aliases = "sell")
  public Component scheduleAuction(
      final Player player,
      final @Arg BigDecimal minimalPrice,
      final @Arg BigDecimal minimalPricePuncture,
      final @Opt Option<Integer> stock
  ) {
    if (!auctionController.whetherAuctionCouldBeScheduled()) {
      return miniMessage().deserialize(
          "<red>Musisz spróbować wystawić przedmiot później, gdyż aktualnie osiągnięty został limit oczekujących aukcji."
      );
    }

    if (!whetherHeldItemIsSupported(player)) {
      return miniMessage().deserialize(
          "<red>Musisz trzymać w łapce przedmiot, który chcesz wystawić na aukcję."
      );
    }

    final int stockOrHeldAmount = stock.orElseGet(
        player.getInventory().getItemInMainHand().getAmount()
    );
    if (stockOrHeldAmount <= 0) {
      return miniMessage().deserialize(
          "<red>Wprowadzony przez ciebie nakład jest nieprawidłowy."
      );
    }

    if (!whetherPlayerContainsStock(
        player,
        player.getInventory().getItemInMainHand(), stockOrHeldAmount)
    ) {
      return miniMessage().deserialize(
          "<red>Wprowadzony przez ciebie nakład przewyższa posiadane przez ciebie przedmioty."
      );
    }

    final BigDecimal fixedMinimalPrice = minimalPrice.setScale(2, HALF_DOWN);
    if (fixedMinimalPrice.compareTo(ZERO) < 0) {
      return miniMessage().deserialize(
          "<red>Wprowadzona przez ciebie kwota startowa jest nieprawidłowa."
      );
    }

    final BigDecimal fixedMinimalPricePuncture = minimalPricePuncture.setScale(2, HALF_DOWN);
    if (fixedMinimalPricePuncture.compareTo(ZERO) <= 0) {
      return miniMessage().deserialize(
          "<red>Wprowadzona przez ciebie kwota przebicia jest nieprawidłowa."
      );
    }

    final ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
    itemStack.setAmount(stockOrHeldAmount);

    player.getInventory().removeItemAnySlot(itemStack);

    auctionController.scheduleAuction(
        new Auction(
            player.getUniqueId(),
            itemStack.serializeAsBytes(),
            minimalPrice,
            minimalPricePuncture
        )
    );
    return miniMessage().deserialize(
        "<gray>Trzymany przez ciebie przedmiot został wystawiony. Aukcja rozpocznie się, gdy nadejdzie jej kolej."
    );
  }

  private boolean whetherHeldItemIsSupported(final Player player) {
    return player.getInventory().getItemInMainHand().getType() != Material.AIR;
  }

  private boolean whetherPlayerContainsStock(
      final Player player, final ItemStack subject, final int stock
  ) {
    return player.getInventory().containsAtLeast(subject, stock);
  }

  @Permission("auroramc.auctions.auction.bid")
  @Execute(route = "bid", aliases = {"buy", "offer"})
  public CompletableFuture<Component> bidAuction(
      final Player player, final @Opt Option<BigDecimal> offer
  ) {
    final Auction auction = auctionController.getOngoingAuction();
    if (auction == null) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Nie możesz złożyć oferty, gdyż w tej chwili nie trwa żadna aukcja."
          )
      );
    }

    if (auction.getVendorUniqueId().equals(player.getUniqueId())) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Nie możesz złożyć oferty, gdyż jest to twoja aukcja."
          )
      );
    }

    if (auction.getTraderUniqueId() != null &&
        auction.getTraderUniqueId().equals(player.getUniqueId())
    ) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Nie możesz złożyć następnej oferty, gdyż twoja oferta jest w tej chwili największa."
          )
      );
    }

    final BigDecimal resolvedOffer = resolveAuctionOffer(auction, offer);
    if (resolvedOffer.compareTo(auction.getMinimalPrice()) < 0) {
      return completedFuture(
          miniMessage()
              .deserialize(
                  "<red>Nie możesz złożyć oferty, gdyż jest ona mniejsza od kwoty startowej."
              )
      );
    }

    if (auction.getCurrentOffer() != null && resolvedOffer.compareTo(auction.getCurrentOffer()) <= 0) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Nie możesz złożyć oferty, gdyż jest ona mniejsza od aktualnej oferty."
          )
      );
    }

    return economyFacade.has(player.getUniqueId(), fundsCurrency, resolvedOffer)
        .thenCompose(whetherTraderHasEnoughMoney ->
            completeBidOfAuction(player, resolvedOffer, whetherTraderHasEnoughMoney)
        )
        .exceptionally(exception ->
            miniMessage().deserialize(
                "<red>Wystąpił błąd podczas składania oferty, spróbuj ponownie."
            )
        );
  }

  private CompletableFuture<Component> completeBidOfAuction(
      final Player trader, final BigDecimal offer, final boolean whetherTraderHasEnoughMoney
  ) {
    if (!whetherTraderHasEnoughMoney) {
      return completedFuture(
          miniMessage().deserialize(
              "<red>Nie posiadasz wystarczająco pieniędzy, aby złożyć tą ofertę."
          )
      );
    }

    final Auction auction = auctionController.getOngoingAuction();
    if (auction.getTraderUniqueId() != null && auction.getCurrentOffer() != null) {
      economyFacade.deposit(
          auction.getTraderUniqueId(), fundsCurrency, auction.getCurrentOffer()
      );
    }

    auction.setTraderUniqueId(trader.getUniqueId());
    auction.setCurrentOffer(offer);
    return economyFacade.withdraw(trader.getUniqueId(), fundsCurrency, offer)
        .thenAccept(state ->
            eventPublisher.publish(new AuctionBidEvent(auction))
        )
        .thenApply(state ->
            miniMessage().deserialize(
                "<gray>Złożyłeś ofertę w wysokości <white><funds_currency><offer><gray>.",
                unparsed("funds_currency", fundsCurrency.getSymbol()),
                unparsed("offer", getFormattedDecimal(offer)))
        );
  }

  private BigDecimal resolveAuctionOffer(Auction auction, Option<BigDecimal> specifiedOffer) {
    return specifiedOffer
        .orElse(
            auction.getCurrentOffer() == null
                ? auction.getMinimalPrice()
                : auction.getCurrentOffer()
                    .add(auction.getMinimalPricePuncture())
        )
        .map(offer -> offer.setScale(2, HALF_DOWN))
        .get();
  }

  @Permission("auroramc.auctions.auction.info")
  @Execute(route = "info", aliases = "view")
  public Component getAuction() {
    final Auction ongoingAuction = auctionController.getOngoingAuction();
    final boolean whetherAuctionIsMissing = ongoingAuction == null;
    if (whetherAuctionIsMissing) {
      return miniMessage().deserialize(
          "<red>W tej chwili nie trwa żadna aukcja."
      );
    }

    return miniMessage().deserialize(
        """
        <gray>Informacje na temat bieżącej <auction_uuid><dark_gray>:
        <dark_gray>► <gray>Przedmiot: <white><subject>
        <dark_gray>► <gray>Osoba wystawiająca: <white><vendor>
        <dark_gray>► <gray>Największa oferta: <highest_bid>
        <dark_gray>► <gray>Minimalna kwota startowa: <white><funds_currency><minimal_price>
        <dark_gray>► <gray>Minimalna kwota przebicia: <white><funds_currency><minimal_price_puncture>
        """.trim(),
        getAuctionSummaryResolvers(ongoingAuction, fundsCurrency)
    );
  }

  @Permission("auroramc.auctions.auction.notifications")
  @Execute(route = "notifications", aliases = {"notification", "notify"})
  public CompletableFuture<Component> toggleNotifications(final Player player) {
    return messageViewerFacade.getMessageViewerByUserUniqueId(player.getUniqueId())
        .thenApply(this::toggleNotifications);
  }

  private Component toggleNotifications(final MessageViewer messageViewer) {
    messageViewer.setWhetherReceiveMessages(!messageViewer.isWhetherReceiveMessages());
    messageViewerFacade.updateMessageViewer(messageViewer);
    return miniMessage().deserialize(
        messageViewer.isWhetherReceiveMessages()
            ? "<gray>Włączyłeś wyświetlanie powiadomień dotyczących aukcji."
            : "<gray>Wyłączyłeś wyświetlanie powiadomień dotyczących aukcji."
    );
  }
}
