package pl.auroramc.auctions.auction;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.AUCTION_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.CONTEXT_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.OFFER_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.TRADER_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.VENDOR_PATH;
import static pl.auroramc.auctions.auction.AuctionUtils.getPotentialOffer;
import static pl.auroramc.commons.concurrent.CompletableFutureUtils.delegateCaughtException;
import static pl.auroramc.commons.concurrent.Mutex.mutex;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.cooldown.Cooldown;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.auctions.AuctionsConfig;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.economy.transaction.TransactionContext;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;

@Permission("auroramc.auctions.auction")
@Command(name = "auction")
@Cooldown(key = "auction-cooldown", count = 3, unit = SECONDS)
public class AuctionCommand {

  private final AudienceFacade audienceFacade;
  private final AuctionMessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;
  private final EconomyFacade economyFacade;
  private final Currency fundsCurrency;
  private final AuctionsConfig auctionsConfig;
  private final AuctionFacade auctionFacade;
  private final AuctionController auctionController;

  public AuctionCommand(
      final AudienceFacade audienceFacade,
      final AuctionMessageSource messageSource,
      final BukkitMessageCompiler messageCompiler,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final AuctionsConfig auctionsConfig,
      final AuctionFacade auctionFacade,
      final AuctionController auctionController) {
    this.audienceFacade = audienceFacade;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
    this.auctionsConfig = auctionsConfig;
    this.auctionFacade = auctionFacade;
    this.auctionController = auctionController;
  }

  @Permission("auroramc.auctions.auction.schedule")
  @Execute(name = "schedule")
  public MutableMessage schedule(
      final @Context Player player,
      final @Arg BigDecimal minimalPrice,
      final @Arg BigDecimal minimalPricePuncture,
      final @OptionalArg Integer stock) {
    if (auctionFacade.getAuctionCount() > auctionsConfig.auctionQueueLimit) {
      return messageSource.auctionQueueIsFull;
    }

    final ItemStack heldItem = player.getInventory().getItemInMainHand();
    if (heldItem.getType() == Material.AIR) {
      return messageSource.requiresHoldingItem;
    }

    final int stockOrHeldAmount = stock == null ? heldItem.getAmount() : stock;
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
            mutex()));

    return messageSource.auctionSchedule;
  }

  @Permission("auroramc.auctions.auction.bid")
  @Execute(name = "bid")
  public MutableMessage bid(final @Context Player player, final @OptionalArg BigDecimal offer) {
    final Auction auction = auctionFacade.getActiveAuction();
    if (auction == null) {
      return messageSource.offerMissingAuction;
    }

    if (auction.getVendorUniqueId().equals(player.getUniqueId())) {
      return messageSource.offerSelfAuction;
    }

    if (auction.getCurrentTraderUniqueId() != null
        && auction.getCurrentTraderUniqueId().equals(player.getUniqueId())) {
      return messageSource.offerIsAlreadyHighest;
    }

    final BigDecimal resolvedOffer = getPotentialOffer(auction, offer);
    if (resolvedOffer.compareTo(auction.getMinimalPrice()) < 0) {
      return messageSource.offerIsAlreadyHighest;
    }

    if (auction.getCurrentOffer() != null
        && resolvedOffer.compareTo(auction.getCurrentOffer()) <= 0) {
      return messageSource.offerIsSmallerThanHighestOffer;
    }

    return economyFacade
        .has(player.getUniqueId(), fundsCurrency, resolvedOffer)
        .thenApply(
            whetherTraderHasEnoughMoney ->
                completeBidOfAuction(player, resolvedOffer, whetherTraderHasEnoughMoney))
        .exceptionally(
            exception -> {
              delegateCaughtException(exception);
              return messageSource.offeringFailed;
            })
        .join();
  }

  private MutableMessage completeBidOfAuction(
      final Player trader, final BigDecimal offer, final boolean whetherTraderHasEnoughMoney) {
    if (!whetherTraderHasEnoughMoney) {
      return messageSource.offerNotEnoughBalance;
    }

    final Auction auction = auctionFacade.getActiveAuction();
    auctionController.setAuctionOffer(auction, trader.getUniqueId(), offer);
    return messageSource.offered.placeholder(
        CONTEXT_PATH, new TransactionContext(fundsCurrency, offer));
  }

  @Permission("auroramc.auctions.auction.summary")
  @Execute(name = "summary")
  public MutableMessage summary() {
    final Auction auction = auctionFacade.getActiveAuction();
    if (auction == null) {
      return messageSource.auctionIsMissing;
    }

    return messageSource
        .auctionSummary
        .placeholder(AUCTION_PATH, auction)
        .placeholder(VENDOR_PATH, getDisplayNameByUniqueId(auction.getVendorUniqueId()))
        .placeholder(OFFER_PATH, getHighestBid(auction, fundsCurrency))
        .placeholder(CURRENCY_PATH, fundsCurrency);
  }

  @Permission("auroramc.auctions.auction.notifications")
  @Execute(name = "notifications")
  public CompletableFuture<MutableMessage> notifications(final @Context Player player) {
    return audienceFacade
        .getAudienceByUniqueId(player.getUniqueId())
        .thenCompose(this::toggleNotifications);
  }

  private CompiledMessage getHighestBid(final Auction auction, final Currency fundsCurrency) {
    if (auction.getCurrentTraderUniqueId() == null) {
      return messageCompiler.compile(messageSource.unknownOffer);
    }

    return messageCompiler.compile(
        messageSource
            .auctionWinningBid
            .placeholder(
                CONTEXT_PATH, new TransactionContext(fundsCurrency, auction.getCurrentOffer()))
            .placeholder(
                TRADER_PATH, getDisplayNameByUniqueId(auction.getCurrentTraderUniqueId())));
  }

  private CompletableFuture<MutableMessage> toggleNotifications(final Audience audience) {
    audience.setAllowsMessages(!audience.isAllowsMessages());
    return audienceFacade
        .updateAudience(audience)
        .thenApply(state -> getNotificationState(audience));
  }

  private MutableMessage getNotificationState(final Audience audience) {
    return audience.isAllowsMessages()
        ? messageSource.notificationsEnabled
        : messageSource.notificationsDisabled;
  }

  private Component getDisplayNameByUniqueId(final UUID playerUniqueId) {
    return Optional.ofNullable(playerUniqueId)
        .map(Bukkit::getPlayer)
        .map(Player::name)
        .orElse(messageCompiler.compile(messageSource.unknownPlayer).getComponent());
  }
}
