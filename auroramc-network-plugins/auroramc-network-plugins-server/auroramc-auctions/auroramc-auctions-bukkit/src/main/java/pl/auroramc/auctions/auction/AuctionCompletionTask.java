package pl.auroramc.auctions.auction;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.Duration.between;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.PERIOD_VARIABLE_KEY;
import static pl.auroramc.auctions.message.MutableMessageVariableKey.UNIQUE_ID_VARIABLE_KEY;

import java.time.Duration;
import pl.auroramc.auctions.message.MessageFacade;
import pl.auroramc.auctions.message.MutableMessageSource;

public class AuctionCompletionTask implements Runnable {

  private static final Duration AUCTION_NOTIFY_SINCE = ofSeconds(3);
  private final MutableMessageSource messageSource;
  private final MessageFacade messageFacade;
  private final AuctionFacade auctionFacade;
  private final AuctionController auctionController;

  public AuctionCompletionTask(
      final MutableMessageSource messageSource,
      final MessageFacade messageFacade,
      final AuctionFacade auctionFacade,
      final AuctionController auctionController
  ) {
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
      if (!auction.whetherAuctionIsAvailable()) {
        auctionController.completeAuction(auction);
        return;
      }

      final Duration period = between(now(), auction.getAvailableUntil());
      if (period.compareTo(AUCTION_NOTIFY_SINCE) < 0) {
        messageFacade.deliverMessageToOnlinePlayers(
            messageSource.auctionNearCompletion
                .with(UNIQUE_ID_VARIABLE_KEY, auction.getAuctionUniqueId())
                .with(
                    PERIOD_VARIABLE_KEY,
                    period
                        .plus(ofSeconds(1))
                        .getSeconds()
                )
        );
      }
    }
  }
}
