package pl.auroramc.auctions.auction;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.UUID.randomUUID;
import static pl.auroramc.auctions.auction.AuctionUtils.getPotentialOffer;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.CURRENCY_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.HIGHEST_BID_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.MINIMAL_PRICE_PUNCTURE_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.MINIMAL_PRICE_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.OFFER_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.SUBJECT_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.TRADER_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.UNIQUE_ID_PATH;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.VENDOR_PATH;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;
import static pl.auroramc.commons.mutex.Mutex.mutex;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
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
import pl.auroramc.auctions.AuctionsConfig;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.auctions.message.MutableMessageSource;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

@Permission("auroramc.auctions.auction")
@Command(name = "auction")
public class AuctionCommand {

  private final Logger logger;
  private final AudienceFacade audienceFacade;
  private final MutableMessageSource messageSource;
  private final EconomyFacade economyFacade;
  private final Currency fundsCurrency;
  private final AuctionsConfig auctionsConfig;
  private final AuctionFacade auctionFacade;
  private final AuctionController auctionController;

  public AuctionCommand(
      final Logger logger,
      final AudienceFacade audienceFacade,
      final MutableMessageSource messageSource,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final AuctionsConfig auctionsConfig,
      final AuctionFacade auctionFacade,
      final AuctionController auctionController
  ) {
    this.logger = logger;
    this.audienceFacade = audienceFacade;
    this.messageSource = messageSource;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
    this.auctionsConfig = auctionsConfig;
    this.auctionFacade = auctionFacade;
    this.auctionController = auctionController;
  }

  @Permission("auroramc.auctions.auction.schedule")
  @Execute(name = "schedule")
  public DeliverableMutableMessage schedule(
      final @Context Player player,
      final @Arg BigDecimal minimalPrice,
      final @Arg BigDecimal minimalPricePuncture,
      final @OptionalArg Integer stock
  ) {
    if (auctionFacade.getAuctionCount() > auctionsConfig.auctionQueueLimit) {
      return messageSource.auctionQueueIsFull;
    }

    final ItemStack heldItem = player.getInventory().getItemInMainHand();
    if (heldItem.getType() == Material.AIR) {
      return messageSource.requiresHoldingItem;
    }

    final int stockOrHeldAmount = stock == null
        ? heldItem.getAmount()
        : stock;
    if (stockOrHeldAmount <= 0) {
      return messageSource.invalidStock;
    }

    if (!player.getInventory().containsAtLeast(heldItem, stockOrHeldAmount)) {
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

    final ItemStack itemStack = heldItem.clone();
    itemStack.setAmount(stockOrHeldAmount);

    player.getInventory().removeItemAnySlot(itemStack);
    auctionFacade.addAuction(
        new Auction(
            randomUUID(),
            player.getUniqueId(),
            itemStack.serializeAsBytes(),
            minimalPrice,
            minimalPricePuncture,
            mutex(),
            mutex()
        )
    );

    return messageSource.auctionSchedule;
  }

  @Permission("auroramc.auctions.auction.bid")
  @Execute(name = "bid")
  public DeliverableMutableMessage bid(
      final @Context Player player,
      final @OptionalArg BigDecimal offer
  ) {
    final Auction auction = auctionFacade.getActiveAuction();
    if (auction == null) {
      return messageSource.offerMissingAuction;
    }

    if (auction.getVendorUniqueId().equals(player.getUniqueId())) {
      return messageSource.offerSelfAuction;
    }

    if (
        auction.getCurrentTraderUniqueId() != null &&
        auction.getCurrentTraderUniqueId().equals(player.getUniqueId())
    ) {
      return messageSource.offerIsAlreadyHighest;
    }

    final BigDecimal resolvedOffer = getPotentialOffer(auction, offer);
    if (resolvedOffer.compareTo(auction.getMinimalPrice()) < 0) {
      return messageSource.offerIsAlreadyHighest;
    }

    if (auction.getCurrentOffer() != null && resolvedOffer.compareTo(auction.getCurrentOffer()) <= 0) {
      return messageSource.offerIsSmallerThanHighestOffer;
    }

    return economyFacade.has(player.getUniqueId(), fundsCurrency, resolvedOffer)
        .thenApply(whetherTraderHasEnoughMoney ->
            completeBidOfAuction(player, resolvedOffer, whetherTraderHasEnoughMoney)
        )
        .exceptionally(exception -> {
          delegateCaughtException(logger, exception);
          return messageSource.offeringFailed;
        })
        .join();
  }

  private DeliverableMutableMessage completeBidOfAuction(
      final Player trader, final BigDecimal offer, final boolean whetherTraderHasEnoughMoney
  ) {
    if (!whetherTraderHasEnoughMoney) {
      return messageSource.offerNotEnoughBalance;
    }

    final Auction auction = auctionFacade.getActiveAuction();
    auctionController.setAuctionOffer(auction, trader.getUniqueId(), offer);
    return messageSource.offered
        .with(CURRENCY_PATH, fundsCurrency.getSymbol())
        .with(OFFER_PATH, getFormattedDecimal(offer));
  }

  @Permission("auroramc.auctions.auction.summary")
  @Execute(name = "summary")
  public DeliverableMutableMessage summary() {
    final Auction auction = auctionFacade.getActiveAuction();
    if (auction == null) {
      return messageSource.auctionIsMissing;
    }

    return messageSource.auctionSummary
        .with(UNIQUE_ID_PATH, auction.getAuctionUniqueId())
        .with(SUBJECT_PATH, getFormattedItemStack(ItemStack.deserializeBytes(auction.getSubject())))
        .with(VENDOR_PATH, getDisplayNameByUniqueId(auction.getVendorUniqueId()))
        .with(CURRENCY_PATH, fundsCurrency.getSymbol())
        .with(HIGHEST_BID_PATH, getHighestBid(auction, fundsCurrency))
        .with(MINIMAL_PRICE_PATH, getFormattedDecimal(auction.getMinimalPrice()))
        .with(MINIMAL_PRICE_PUNCTURE_PATH, getFormattedDecimal(auction.getMinimalPricePuncture()));
  }

  @Permission("auroramc.auctions.auction.notifications")
  @Execute(name = "notifications")
  public CompletableFuture<DeliverableMutableMessage> notifications(final @Context Player player) {
    return audienceFacade.getAudienceByUniqueId(player.getUniqueId())
        .thenApply(this::toggleNotifications);
  }

  private DeliverableMutableMessage toggleNotifications(final Audience audience) {
    audience.setAllowsMessages(!audience.isAllowsMessages());
    audienceFacade.updateAudience(audience);
    return audience.isAllowsMessages()
        ? messageSource.notificationsEnabled
        : messageSource.notificationsDisabled;
  }

  private DeliverableMutableMessage getHighestBid(final Auction auction, final Currency fundsCurrency) {
    if (auction.getCurrentTraderUniqueId() == null) {
      return messageSource.unknownOffer;
    }

    return messageSource.auctionWinningBid
        .with(CURRENCY_PATH, fundsCurrency.getSymbol())
        .with(OFFER_PATH, getFormattedDecimal(auction.getCurrentOffer()))
        .with(TRADER_PATH, getDisplayNameByUniqueId(auction.getCurrentTraderUniqueId()));
  }

  private Component getDisplayNameByUniqueId(final UUID playerUniqueId) {
    return Optional.ofNullable(playerUniqueId)
        .map(Bukkit::getPlayer)
        .map(Player::name)
        .orElse(
            messageSource.unknownPlayer
                .compile()
        );
  }
}
