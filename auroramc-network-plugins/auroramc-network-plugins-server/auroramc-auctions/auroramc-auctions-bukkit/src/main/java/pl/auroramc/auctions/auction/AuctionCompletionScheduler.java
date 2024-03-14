package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;

import java.time.Duration;
import java.time.Instant;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.message.MessageSource;

public class AuctionCompletionScheduler implements Runnable {

  private static final Duration AUCTION_NOTIFY_SINCE = ofSeconds(3);
  private final MessageSource messageSource;
  private final MessageFacade messageFacade;
  private final AuctionController auctionController;

  public AuctionCompletionScheduler(
      final MessageSource messageSource,
      final MessageFacade messageFacade,
      final AuctionController auctionController
  ) {
    this.messageSource = messageSource;
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
            messageSource.auctionNearCompletion
                .with("unique_id", auction.getAuctionUniqueId())
                .with("period", remainingDurationOfAuction.getSeconds())
        );
      }
    }
  }
}
