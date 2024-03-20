package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.CURRENCY_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.CURRENT_OFFER_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.CURRENT_TRADER_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.MINIMAL_PRICE_PUNCTURE_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.MINIMAL_PRICE_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.OFFSET_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.SUBJECT_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.UNIQUE_ID_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.VENDOR_VARIABLE_KEY;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import pl.auroramc.auctions.AuctionsConfig;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.message.MutableMessageSource;
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class AuctionController {

  private static final Duration AUCTION_BIDDING_OFFSET_APPLYING_SINCE = ofSeconds(5);
  private static final Duration AUCTION_BIDDING_OFFSET = ofSeconds(3);
  private final Logger logger;
  private final AuctionsConfig auctionsConfig;
  private final AuctionFacade auctionFacade;
  private final MutableMessageSource messageSource;
  private final MessageFacade messageFacade;
  private final EconomyFacade economyFacade;
  private final VaultController vaultController;
  private final Currency fundsCurrency;
  private final UserFacade userFacade;

  public AuctionController(
      final Logger logger,
      final AuctionsConfig auctionsConfig,
      final AuctionFacade auctionFacade,
      final MutableMessageSource messageSource,
      final MessageFacade messageFacade,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final VaultController vaultController,
      final UserFacade userFacade) {
    this.logger = logger;
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
                        .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
                        .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol())
                        .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(auction.getSubject()))
                        .with(CURRENT_TRADER_VARIABLE_KEY, traderName)
                        .with(
                            CURRENT_OFFER_VARIABLE_KEY,
                            getFormattedDecimal(auction.getCurrentOffer()))))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void extendAuction(final Auction auction) {
    final Duration period = between(now(), auction.getAvailableUntil());
    if (period.compareTo(AUCTION_BIDDING_OFFSET_APPLYING_SINCE) < 0) {
      auction.setAvailableUntil(auction.getAvailableUntil().plus(AUCTION_BIDDING_OFFSET));
      messageFacade.deliverMessage(
          messageSource
              .auctionHasBeenExtended
              .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
              .with(OFFSET_VARIABLE_KEY, AUCTION_BIDDING_OFFSET.getSeconds()));
    }
  }

  private void scheduleAuction(final Auction auction, final Component vendorName) {
    auctionFacade.setActiveAuction(auction);
    auction.setAvailableSince(now());
    auction.setAvailableUntil(auction.getAvailableSince().plus(auctionsConfig.auctioningPeriod));
    messageFacade.deliverMessage(
        messageSource
            .auctionHasStarted
            .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
            .with(VENDOR_VARIABLE_KEY, vendorName)
            .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(auction.getSubject()))
            .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol())
            .with(MINIMAL_PRICE_VARIABLE_KEY, getFormattedDecimal(auction.getMinimalPrice()))
            .with(
                MINIMAL_PRICE_PUNCTURE_VARIABLE_KEY,
                getFormattedDecimal(auction.getMinimalPricePuncture())));
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
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void completeAuction(
      final Auction auction, final Component vendorName, final Component traderName) {
    checkNotNull(auction.getCurrentOffer());

    vaultController.createVaultItem(auction.getCurrentTraderUniqueId(), auction.getSubject());
    messageFacade.deliverMessage(
        messageSource
            .auctionHasCompleted
            .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
            .with(CURRENCY_VARIABLE_KEY, fundsCurrency.getSymbol())
            .with(VENDOR_VARIABLE_KEY, vendorName)
            .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(auction.getSubject()))
            .with(CURRENT_OFFER_VARIABLE_KEY, getFormattedDecimal(auction.getCurrentOffer()))
            .with(CURRENT_TRADER_VARIABLE_KEY, traderName));
  }

  private void restoreSubject(final Auction auction, final Component vendorName) {
    vaultController.createVaultItem(auction.getVendorUniqueId(), auction.getSubject());
    messageFacade.deliverMessage(
        messageSource
            .auctionHasCompletedWithoutOffers
            .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
            .with(VENDOR_VARIABLE_KEY, vendorName));
  }

  private void getUsernameByUniqueIdAndDelegate(
      final Auction auction, final BiConsumer<Auction, Component> delegator) {
    getUsernameByUniqueId(auction.getVendorUniqueId())
        .thenAccept(vendorName -> delegator.accept(auction, vendorName))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private CompletableFuture<TextComponent> getUsernameByUniqueId(final UUID uniqueId) {
    return userFacade
        .getUserByUniqueId(uniqueId)
        .thenApply(User::getUsername)
        .thenApply(Component::text)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
