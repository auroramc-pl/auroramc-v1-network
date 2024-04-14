package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.AUCTION_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.CURRENCY_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.DURATION_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.TRADER_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.VENDOR_PATH;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import pl.auroramc.auctions.AuctionsConfig;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.economy.economy.EconomyFacade;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class AuctionController {

  private static final Duration AUCTION_BIDDING_OFFSET_APPLYING_SINCE = ofSeconds(5);
  private static final Duration AUCTION_BIDDING_OFFSET = ofSeconds(3);
  private final AuctionsConfig auctionsConfig;
  private final AuctionFacade auctionFacade;
  private final AuctionMessageSource messageSource;
  private final MessageFacade messageFacade;
  private final EconomyFacade economyFacade;
  private final VaultController vaultController;
  private final Currency fundsCurrency;
  private final UserFacade userFacade;

  public AuctionController(
      final AuctionsConfig auctionsConfig,
      final AuctionFacade auctionFacade,
      final AuctionMessageSource messageSource,
      final MessageFacade messageFacade,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final VaultController vaultController,
      final UserFacade userFacade) {
    this.auctionsConfig = auctionsConfig;
    this.auctionFacade = auctionFacade;
    this.messageSource = messageSource;
    this.messageFacade = messageFacade;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
    this.vaultController = vaultController;
    this.userFacade = userFacade;
  }

  public void scheduleAuction(final Auction auction) {
    getUsernameByUniqueIdAndDelegate(auction, this::scheduleAuction);
  }

  public void completeAuction(final Auction auction) {
    getUsernameByUniqueIdAndDelegate(auction, this::completeAuction);
  }

  public void setAuctionOffer(
      final Auction auction, final UUID traderUniqueId, final BigDecimal offer) {
    checkNotNull(auction.getAvailableSince());
    checkNotNull(auction.getAvailableUntil());

    auction.setCurrentTraderUniqueId(traderUniqueId);
    auction.setCurrentOffer(offer);

    extendAuction(auction);

    getUsernameByUniqueId(traderUniqueId)
        .thenAccept(
            traderName ->
                messageFacade.deliverMessage(
                    messageSource
                        .auctionReceivedOffer
                        .placeholder(AUCTION_PATH, auction)
                        .placeholder(TRADER_PATH, traderName)
                        .placeholder(CURRENCY_PATH, fundsCurrency)))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void extendAuction(final Auction auction) {
    final Duration period = between(now(), auction.getAvailableUntil());
    if (period.compareTo(AUCTION_BIDDING_OFFSET_APPLYING_SINCE) < 0) {
      auction.setAvailableUntil(auction.getAvailableUntil().plus(AUCTION_BIDDING_OFFSET));
      messageFacade.deliverMessage(
          messageSource
              .auctionHasBeenExtended
              .placeholder(AUCTION_PATH, auction)
              .placeholder(DURATION_PATH, AUCTION_BIDDING_OFFSET));
    }
  }

  private void scheduleAuction(final Auction auction, final Component vendorName) {
    auctionFacade.setActiveAuction(auction);
    auction.setAvailableSince(now());
    auction.setAvailableUntil(auction.getAvailableSince().plus(auctionsConfig.auctioningPeriod));
    messageFacade.deliverMessage(
        messageSource
            .auctionHasStarted
            .placeholder(AUCTION_PATH, auction)
            .placeholder(VENDOR_PATH, vendorName)
            .placeholder(CURRENCY_PATH, fundsCurrency));
  }

  private void completeAuction(final Auction auction, final Component vendorName) {
    auctionFacade.setActiveAuction(null);
    if (auction.getCurrentTraderUniqueId() == null) {
      restoreSubject(auction, vendorName);
      return;
    }

    economyFacade
        .transfer(
            auction.getCurrentTraderUniqueId(),
            auction.getVendorUniqueId(),
            fundsCurrency,
            auction.getCurrentOffer())
        .thenCompose(state -> getUsernameByUniqueId(auction.getCurrentTraderUniqueId()))
        .thenAccept(traderName -> completeAuction(auction, vendorName, traderName))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private void completeAuction(
      final Auction auction, final Component vendorName, final Component traderName) {
    checkNotNull(auction.getCurrentOffer());

    vaultController.createVaultItem(auction.getCurrentTraderUniqueId(), auction.getSubject());
    messageFacade.deliverMessage(
        messageSource
            .auctionHasCompleted
            .placeholder(AUCTION_PATH, auction)
            .placeholder(VENDOR_PATH, vendorName)
            .placeholder(TRADER_PATH, traderName)
            .placeholder(CURRENCY_PATH, fundsCurrency));
  }

  private void restoreSubject(final Auction auction, final Component vendorName) {
    vaultController.createVaultItem(auction.getVendorUniqueId(), auction.getSubject());
    messageFacade.deliverMessage(
        messageSource
            .auctionHasCompletedWithoutOffers
            .placeholder(AUCTION_PATH, auction)
            .placeholder(VENDOR_PATH, vendorName));
  }

  private void getUsernameByUniqueIdAndDelegate(
      final Auction auction, final BiConsumer<Auction, Component> delegator) {
    getUsernameByUniqueId(auction.getVendorUniqueId())
        .thenAccept(vendorName -> delegator.accept(auction, vendorName))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<TextComponent> getUsernameByUniqueId(final UUID uniqueId) {
    return userFacade
        .getUserByUniqueId(uniqueId)
        .thenApply(User::getUsername)
        .thenApply(Component::text)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
