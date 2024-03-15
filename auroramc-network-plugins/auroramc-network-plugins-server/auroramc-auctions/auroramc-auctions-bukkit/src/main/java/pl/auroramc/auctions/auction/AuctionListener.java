package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.auroramc.auctions.message.MessageVariableKey.CURRENT_OFFER_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.CURRENT_TRADER_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.MINIMAL_PRICE_PUNCTURE_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.MINIMAL_PRICE_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.OFFSET_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.SUBJECT_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.SYMBOL_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.UNIQUE_ID_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MessageVariableKey.VENDOR_VARIABLE_KEY;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.auroramc.auctions.auction.event.BaseAuctionEvent;
import pl.auroramc.auctions.auction.event.AuctionBidEvent;
import pl.auroramc.auctions.auction.event.AuctionCompleteEvent;
import pl.auroramc.auctions.auction.event.AuctionScheduleEvent;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.message.MessageSource;
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class AuctionListener implements Listener {

  private static final Duration AUCTION_BIDDING_OFFSET_APPLYING_SINCE = Duration.ofSeconds(5);
  private static final Duration AUCTION_BIDDING_OFFSET = Duration.ofSeconds(3);
  private final Logger logger;
  private final UserFacade userFacade;
  private final MessageSource messageSource;
  private final MessageFacade messageFacade;
  private final EconomyFacade economyFacade;
  private final Currency fundsCurrency;
  private final VaultController vaultController;
  private final AuctionController auctionController;

  public AuctionListener(
      final Logger logger,
      final UserFacade userFacade,
      final MessageSource messageSource,
      final MessageFacade messageFacade,
      final EconomyFacade economyFacade,
      final Currency fundsCurrency,
      final VaultController vaultController,
      final AuctionController auctionController
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.messageSource = messageSource;
    this.messageFacade = messageFacade;
    this.economyFacade = economyFacade;
    this.fundsCurrency = fundsCurrency;
    this.vaultController = vaultController;
    this.auctionController = auctionController;
  }

  @EventHandler
  public void onAuctionSchedule(final AuctionScheduleEvent event) {
    resolveVendorNameAndDelegate(event, this::handleAuctionScheduling)
        .thenAccept(state -> auctionController.setOngoingAuction(event.getAuction()))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @EventHandler
  public void onAuctionComplete(final AuctionCompleteEvent event) {
    resolveVendorNameAndDelegate(event, this::handleAuctionCompleting)
        .thenAccept(state -> {
          auctionController.setOngoingAuction(null);
          auctionController.attemptAuctionRetrievalWithScheduling();
        })
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @EventHandler
  public void onAuctionBid(final AuctionBidEvent event) {
    resolveVendorNameAndDelegate(event, this::handleAuctionBidding)
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void handleAuctionScheduling(
      final AuctionScheduleEvent event, final Component vendorName
  ) {
    final Auction auction = event.getAuction();
    messageFacade.deliverMessageToOnlinePlayers(
        messageSource.auctionHasStarted
            .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
            .with(VENDOR_VARIABLE_KEY, vendorName)
            .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(auction.getSubject()))
            .with(MINIMAL_PRICE_VARIABLE_KEY, getFormattedDecimal(auction.getMinimalPrice()))
            .with(
                MINIMAL_PRICE_PUNCTURE_VARIABLE_KEY, getFormattedDecimal(auction.getMinimalPricePuncture())
            )
    );
  }

  private void handleAuctionCompleting(
      final AuctionCompleteEvent event, final Component vendorName
  ) {
    if (event.getAuction().getTraderUniqueId() == null) {
      vaultController.createVaultItem(
          event.getAuction().getVendorUniqueId(),
          event.getSubject().serializeAsBytes()
      );
      messageFacade.deliverMessageToOnlinePlayers(
          messageSource.auctionHasCompletedWithoutOffers
              .with(UNIQUE_ID_VARIABLE_KEY, event.getAuction().getAuctionUniqueId())
              .with(VENDOR_VARIABLE_KEY, vendorName)
      );
      return;
    }

    userFacade.getUserByUniqueId(event.getAuction().getTraderUniqueId())
        .thenApply(User::getUsername)
        .thenApply(Component::text)
        .thenAccept(traderName -> handleAuctionCompletingWithTrader(event, vendorName, traderName))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void handleAuctionBidding(
      final AuctionBidEvent event, final Component vendorName
  ) {
    userFacade.getUserByUniqueId(event.getAuction().getTraderUniqueId())
        .thenApply(User::getUsername)
        .thenApply(Component::text)
        .thenAccept(traderName -> handleAuctionBiddingWithTrader(event, traderName))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void handleAuctionBiddingWithTrader(
      final AuctionBidEvent event, final Component traderName
  ) {
    checkNotNull(event.getAuction().getAvailableSince());
    checkNotNull(event.getAuction().getAvailableUntil());

    messageFacade.deliverMessageToOnlinePlayers(
        messageSource.auctionReceivedOffer
            .with(UNIQUE_ID_VARIABLE_KEY, event.getAuction().getAuctionUniqueId())
            .with(SYMBOL_VARIABLE_KEY, fundsCurrency.getSymbol())
            .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(event.getSubject()))
            .with(CURRENT_TRADER_VARIABLE_KEY, traderName)
            .with(CURRENT_OFFER_VARIABLE_KEY, getFormattedDecimal(event.getAuction().getCurrentOffer()))
    );

    final Duration remainingDurationOfAuction = Duration.between(Instant.now(), event.getAuction().getAvailableUntil());
    if (remainingDurationOfAuction.compareTo(AUCTION_BIDDING_OFFSET_APPLYING_SINCE) < 0) {
      event.getAuction().setAvailableUntil(
          event.getAuction()
              .getAvailableUntil()
              .plus(AUCTION_BIDDING_OFFSET)
      );

      messageFacade.deliverMessageToOnlinePlayers(
          messageSource.auctionHasBeenExtended
              .with(UNIQUE_ID_VARIABLE_KEY, event.getAuction().getAuctionUniqueId())
              .with(OFFSET_VARIABLE_KEY, AUCTION_BIDDING_OFFSET.getSeconds())
      );
    }
  }

  private void handleAuctionCompletingWithTrader(
      final AuctionCompleteEvent event,
      final Component vendorName,
      final Component traderName
  ) {
    checkNotNull(event.getAuction().getCurrentOffer());

    event.getVendor().map(Player::getUniqueId)
        .ifPresent(uniqueId ->
            economyFacade
                .deposit(uniqueId, fundsCurrency, event.getAuction().getCurrentOffer())
                .exceptionally(exception -> delegateCaughtException(logger, exception))
        );

    vaultController.createVaultItem(
        event.getAuction().getTraderUniqueId(),
        event.getSubject().serializeAsBytes()
    );
    messageFacade.deliverMessageToOnlinePlayers(
        messageSource.auctionHasCompleted
            .with(UNIQUE_ID_VARIABLE_KEY, event.getAuction().getAuctionUniqueId())
            .with(SYMBOL_VARIABLE_KEY, fundsCurrency.getSymbol())
            .with(VENDOR_VARIABLE_KEY, vendorName)
            .with(SUBJECT_VARIABLE_KEY, getFormattedItemStack(event.getSubject()))
            .with(
                CURRENT_OFFER_VARIABLE_KEY, getFormattedDecimal(event.getAuction().getCurrentOffer())
            )
            .with(CURRENT_TRADER_VARIABLE_KEY, traderName)
    );
  }

  private <T extends BaseAuctionEvent> CompletableFuture<Void> resolveVendorNameAndDelegate(
      final T event, final BiConsumer<T, Component> resolver
  ) {
    return userFacade.getUserByUniqueId(event.getAuction().getVendorUniqueId())
        .thenApply(User::getUsername)
        .thenApply(Component::text)
        .thenAccept(vendorName -> resolver.accept(event, vendorName))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
