package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static pl.auroramc.auctions.auction.AuctionUtils.getAuctionNotifyingResolvers;

import java.time.Duration;
import java.time.Instant;
import pl.auroramc.auctions.message.MessageFacade;

public class AuctionCompletionScheduler implements Runnable {

  private static final Duration AUCTION_NOTIFY_SINCE = ofSeconds(3);
  private final MessageFacade messageFacade;
  private final AuctionController auctionController;

  public AuctionCompletionScheduler(
      final MessageFacade messageFacade,
      final AuctionController auctionController
  ) {
    this.messageFacade = messageFacade;
    this.auctionController = auctionController;
  }

  @Override
  public void run() {
    if (auctionController.whetherAnyAuctionIsActive()) {
      final Auction auction = auctionController.getOngoingAuction();

      checkNotNull(auction.getAvailableSince());
      checkNotNull(auction.getAvailableUntil());

      if (!auction.whetherAuctionIsAvailable()) {
        auctionController.completeAuction();
        return;
      }

      final Duration remainingDurationOfAuction = between(Instant.now(), auction.getAvailableUntil());
      if (remainingDurationOfAuction.compareTo(AUCTION_NOTIFY_SINCE) < 0) {
        messageFacade.deliverMessageToOnlinePlayers(
            miniMessage().deserialize(
                "<gray><auction_uuid> zostanie zakończona za <white><period> <gray>sekund.",
                getAuctionNotifyingResolvers(
                    auction,
                    text("Aukcja"),
                    remainingDurationOfAuction.plusSeconds(1)
                )
            )
        );
      }
    }
  }
}
