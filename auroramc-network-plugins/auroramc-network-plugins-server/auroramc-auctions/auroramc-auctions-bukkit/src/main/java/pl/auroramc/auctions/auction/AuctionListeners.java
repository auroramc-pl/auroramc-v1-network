package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionBiddingResolvers;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionCompletingResolvers;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionCompletingResolversWithTrader;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionSchedulingResolvers;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionUuidWithHoverDisplay;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

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
import pl.auroramc.auctions.vault.VaultController;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.registry.user.User;
import pl.auroramc.registry.user.UserFacade;

public class AuctionListeners implements Listener {

  private static final Duration AUCTION_BIDDING_OFFSET_APPLYING_SINCE = Duration.ofSeconds(5);
  private static final Duration AUCTION_BIDDING_OFFSET = Duration.ofSeconds(3);
  private final Logger logger;
  private final UserFacade userFacade;
  private final MessageFacade messageFacade;
  private final EconomyFacade economyFacade;
  private final Currency primaryCurrency;
  private final VaultController vaultController;
  private final AuctionController auctionController;

  public AuctionListeners(
      final Logger logger,
      final UserFacade userFacade,
      final MessageFacade messageFacade,
      final EconomyFacade economyFacade,
      final Currency primaryCurrency,
      final VaultController vaultController,
      final AuctionController auctionController
  ) {
    this.logger = logger;
    this.userFacade = userFacade;
    this.messageFacade = messageFacade;
    this.economyFacade = economyFacade;
    this.primaryCurrency = primaryCurrency;
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
    messageFacade.deliverMessageToOnlinePlayers(miniMessage().deserialize(
        "<gray>Gracz <white><vendor></white> rozpoczął <auction_uuid> o przedmiot <white><subject></white>. Kwota początkowa wynosi <white>$<min_offer></white>, a minimalna kwota przebicia to <white>$<min_offer_bid></white>.",
        getAuctionSchedulingResolvers(event, vendorName)));
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
          miniMessage().deserialize(
              "<gray><auction_uuid> zakończyła się bez ofert. Przedmiot został zwrócony do <white><vendor></white>.",
              getAuctionCompletingResolvers(event, vendorName)
          )
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
        .thenAccept(traderName -> handleAuctionBiddingWithTrader(event, vendorName, traderName))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  private void handleAuctionBiddingWithTrader(
      final AuctionBidEvent event,
      final Component vendorName,
      final Component traderName
  ) {
    checkNotNull(event.getAuction().getAvailableSince());
    checkNotNull(event.getAuction().getAvailableUntil());

    messageFacade.deliverMessageToOnlinePlayers(
        miniMessage().deserialize(
            "<gray>Gracz <white><current_trader></white> złożył ofertę <white>$<current_offer></white> na <auction_uuid> o przedmiot <white><subject></white>.",
            getAuctionBiddingResolvers(event, vendorName, traderName)
        )
    );

    final Duration remainingDurationOfAuction = Duration.between(Instant.now(), event.getAuction().getAvailableUntil());
    if (remainingDurationOfAuction.compareTo(AUCTION_BIDDING_OFFSET_APPLYING_SINCE) < 0) {
      event.getAuction().setAvailableUntil(
          event.getAuction()
              .getAvailableUntil()
              .plus(AUCTION_BIDDING_OFFSET)
      );

      messageFacade.deliverMessageToOnlinePlayers(
          miniMessage().deserialize(
              "<gray>Czas trwania <auction_uuid> został przedłużony o <white><auction_offset></white>.",
              unparsed("auction_offset", "%ds".formatted(AUCTION_BIDDING_OFFSET.getSeconds())),
              component("auction_uuid",
                  text("aukcji")
                      .hoverEvent(getAuctionUuidWithHoverDisplay(event.getAuction()))
              )
          )
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
                .deposit(uniqueId, primaryCurrency, event.getAuction().getCurrentOffer())
                .exceptionally(exception -> delegateCaughtException(logger, exception))
        );

    vaultController.createVaultItem(
        event.getAuction().getTraderUniqueId(),
        event.getSubject().serializeAsBytes()
    );
    messageFacade.deliverMessageToOnlinePlayers(
        miniMessage().deserialize(
            "<gray><auction_uuid> gracza <white><vendor></white> zakończyła się. Przedmiot <white><subject></white> został sprzedany za <white>$<current_offer></white> graczowi <white><current_trader></white>.",
            getAuctionCompletingResolversWithTrader(event, vendorName, traderName)
        )
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
