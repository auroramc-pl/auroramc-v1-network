package pl.auroramc.auctions.auction;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.option.Opt;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;
import pl.auroramc.auctions.auction.event.AuctionBidEvent;
import pl.auroramc.auctions.message.MessageSource;
import pl.auroramc.auctions.message.viewer.MessageViewer;
import pl.auroramc.auctions.message.viewer.MessageViewerFacade;
import pl.auroramc.commons.event.publisher.EventPublisher;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

@Permission("auroramc.auctions.auction")
@Route(name = "auction")
public class AuctionCommand {

  private final Logger logger;
  private final MessageViewerFacade messageViewerFacade;
  private final MessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final Currency fundsCurrency;
  private final AuctionController auctionController;
  private final EventPublisher eventPublisher;

  public AuctionCommand(
      final Logger logger,
      final MessageViewerFacade messageViewerFacade,
      final MessageSource messageSource,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final AuctionController auctionController,
      final EventPublisher eventPublisher
  ) {
    this.logger = logger;
    this.messageViewerFacade = messageViewerFacade;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
    this.auctionController = auctionController;
    this.eventPublisher = eventPublisher;
  }

  @Permission("auroramc.auctions.auction.schedule")
  @Execute(route = "schedule")
  public MutableMessage schedule(
      final Player player,
      final @Arg BigDecimal minimalPrice,
      final @Arg BigDecimal minimalPricePuncture,
      final @Opt Option<Integer> stock
  ) {
    if (!auctionController.whetherAuctionCouldBeScheduled()) {
      return messageSource.auctionQueueIsFull;
    }

    if (!whetherHeldItemIsSupported(player)) {
      return messageSource.requiresHoldingItem;
    }

    final int stockOrHeldAmount = stock.orElseGet(
        player.getInventory().getItemInMainHand().getAmount()
    );
    if (stockOrHeldAmount <= 0) {
      return messageSource.invalidStock;
    }

    if (!whetherPlayerContainsStock(
        player,
        player.getInventory().getItemInMainHand(), stockOrHeldAmount)
    ) {
      return messageSource.invalidStockBecauseOfMissingItems;
    }

    final BigDecimal fixedMinimalPrice = minimalPrice.setScale(2, HALF_DOWN);
    if (fixedMinimalPrice.compareTo(ZERO) < 0) {
      return messageSource.invalidMinimalPrice;
    }

    final BigDecimal fixedMinimalPricePuncture = minimalPricePuncture.setScale(2, HALF_DOWN);
    if (fixedMinimalPricePuncture.compareTo(ZERO) <= 0) {
      return messageSource.invalidMinimalPricePuncture;
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
    return messageSource.auctionSchedule;
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
  @Execute(route = "bid")
  public CompletableFuture<MutableMessage> bid(
      final Player player, final @Opt Option<BigDecimal> offer
  ) {
    final Auction auction = auctionController.getOngoingAuction();
    if (auction == null) {
      return messageSource.offerMissingAuction
          .asCompletedFuture();
    }

    if (auction.getVendorUniqueId().equals(player.getUniqueId())) {
      return messageSource.offerSelfAuction
          .asCompletedFuture();
    }

    if (
        auction.getTraderUniqueId() != null &&
        auction.getTraderUniqueId().equals(player.getUniqueId())
    ) {
      return messageSource.offerIsAlreadyHighest
          .asCompletedFuture();
    }

    final BigDecimal resolvedOffer = resolveAuctionOffer(auction, offer);
    if (resolvedOffer.compareTo(auction.getMinimalPrice()) < 0) {
      return messageSource.offerIsAlreadyHighest
          .asCompletedFuture();
    }

    if (
        auction.getCurrentOffer() != null &&
        resolvedOffer.compareTo(auction.getCurrentOffer()) <= 0
    ) {
      return messageSource.offerIsSmallerThanHighestOffer
          .asCompletedFuture();
    }

    return economyFacade.has(player.getUniqueId(), fundsCurrency, resolvedOffer)
        .thenCompose(whetherTraderHasEnoughMoney ->
            completeBidOfAuction(player, resolvedOffer, whetherTraderHasEnoughMoney)
        )
        .exceptionally(exception -> {
          delegateCaughtException(logger, exception);
          return messageSource.offeringFailure;
        });
  }

  private CompletableFuture<MutableMessage> completeBidOfAuction(
      final Player trader, final BigDecimal offer, final boolean whetherTraderHasEnoughMoney
  ) {
    if (!whetherTraderHasEnoughMoney) {
      return messageSource.offerNotEnoughBalance
          .asCompletedFuture();
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
            messageSource.offered
                .with("symbol", fundsCurrency.getSymbol())
                .with("offer", getFormattedDecimal(offer))
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

  @Permission("auroramc.auctions.auction.summary")
  @Execute(route = "summary")
  public MutableMessage summary() {
    final Auction auction = auctionController.getOngoingAuction();
    if (auction == null) {
      return messageSource.auctionIsMissing;
    }

    return messageSource.auctionSummary
        .with("unique_id", auction.getAuctionUniqueId())
        .with("subject", getFormattedItemStack(ItemStack.deserializeBytes(auction.getSubject())))
        .with("vendor", getDisplayNameByUniqueId(auction.getVendorUniqueId()))
        .with("highest_bid", getHighestBid(auction, fundsCurrency))
        .with("symbol", fundsCurrency.getSymbol())
        .with("minimal_price", getFormattedDecimal(auction.getMinimalPrice()))
        .with("minimal_price_puncture", getFormattedDecimal(auction.getMinimalPricePuncture()));
  }

  @Permission("auroramc.auctions.auction.notifications")
  @Execute(route = "notifications", aliases = {"notification", "notify"})
  public CompletableFuture<MutableMessage> notifications(final Player player) {
    return messageViewerFacade.getMessageViewerByUserUniqueId(player.getUniqueId())
        .thenApply(this::toggleNotifications);
  }

  private MutableMessage toggleNotifications(final MessageViewer messageViewer) {
    messageViewer.setWhetherReceiveMessages(!messageViewer.isWhetherReceiveMessages());
    messageViewerFacade.updateMessageViewer(messageViewer);
    return messageViewer.isWhetherReceiveMessages()
        ? messageSource.notificationsEnabled
        : messageSource.notificationsDisabled;
  }

  private MutableMessage getHighestBid(final Auction auction, final Currency fundsCurrency) {
    if (auction.getTraderUniqueId() == null) {
      return messageSource.unknownOffer;
    }

    return messageSource.auctionWinningBid
        .with("symbol", fundsCurrency.getSymbol())
        .with("offer", getFormattedDecimal(auction.getCurrentOffer()))
        .with("trader", getDisplayNameByUniqueId(auction.getTraderUniqueId()));
  }

  private Component getDisplayNameByUniqueId(final UUID playerUniqueId) {
    return Optional.ofNullable(playerUniqueId)
        .map(Bukkit::getPlayer)
        .map(Player::name)
        .orElse(messageSource.unknownPlayer.compile());
  }
}
