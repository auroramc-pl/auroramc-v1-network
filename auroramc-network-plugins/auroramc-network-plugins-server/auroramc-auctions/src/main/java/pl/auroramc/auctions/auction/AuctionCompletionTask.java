package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.AUCTION_PATH;
import static pl.auroramc.auctions.auction.AuctionMessageSourcePaths.DURATION_PATH;

import java.time.Duration;
import pl.auroramc.auctions.message.MessageFacade;

public class AuctionCompletionTask implements Runnable {

  private static final Duration AUCTION_NOTIFY_SINCE = ofSeconds(3);
  private final AuctionMessageSource messageSource;
  private final MessageFacade messageFacade;
  private final AuctionFacade auctionFacade;
  private final AuctionController auctionController;

  public AuctionCompletionTask(
      final AuctionMessageSource messageSource,
      final MessageFacade messageFacade,
      final AuctionFacade auctionFacade,
      final AuctionController auctionController) {
    this.messageSource = messageSource;
    this.messageFacade = messageFacade;
    this.auctionFacade = auctionFacade;
    this.auctionController = auctionController;
  }

  @Override
  public void run() {
    final Auction auction = auctionFacade.getActiveAuction();
    if (auction != null) {
      checkNotNull(auction.getAvailableSince());
      checkNotNull(auction.getAvailableUntil());
      final Duration period = between(now(), auction.getAvailableUntil());
      if (period.compareTo(AUCTION_NOTIFY_SINCE) < 0 && auction.getRemainingTicks() > 0) {
        messageFacade.deliverMessage(
            messageSource
                .auctionNearCompletion
                .placeholder(AUCTION_PATH, auction)
                .placeholder(DURATION_PATH, ofSeconds(auction.getAndDecrementRemainingTicks())));
      }

      if (!auction.whetherAuctionIsAvailable()) {
        auctionController.completeAuction(auction);
      }
    }
  }
}
