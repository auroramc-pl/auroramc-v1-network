package pl.auroramc.auctions.auction;

import java.time.Instant;
import pl.auroramc.auctions.AuctionsConfig;
import pl.auroramc.auctions.auction.event.AuctionCompleteEvent;
import pl.auroramc.auctions.auction.event.AuctionScheduleEvent;
import pl.auroramc.commons.event.publisher.EventPublisher;

public class AuctionController {

  private final AuctionsConfig auctionsConfig;
  private final AuctionFacade auctionFacade;
  private final EventPublisher eventPublisher;
  private Auction ongoingAuction;

  public AuctionController(
      final AuctionsConfig auctionsConfig,
      final AuctionFacade auctionFacade,
      final EventPublisher eventPublisher
  ) {
    this.auctionsConfig = auctionsConfig;
    this.auctionFacade = auctionFacade;
    this.eventPublisher = eventPublisher;
  }

  public void attemptAuctionRetrievalWithScheduling() {
    if (whetherAnyAuctionIsActive()) {
      return;
    }

    final Instant now = Instant.now();
    if (whetherAnyAuctionIsScheduled()) {
      final Auction auction = auctionFacade.getNextAuction();
      auction.setAvailableSince(now);
      auction.setAvailableUntil(now.plus(auctionsConfig.auctioningPeriod));
      eventPublisher.publish(new AuctionScheduleEvent(auction));
    }
  }

  public void scheduleAuction(final Auction auction) {
    if (whetherAuctionCouldBeScheduled()) {
      auctionFacade.queueAuction(auction);
      attemptAuctionRetrievalWithScheduling();
      return;
    }

    throw new AuctionSchedulingException(
        "Auction could not be scheduled, because of exceeding limit of scheduled auctions."
    );
  }

  public void completeAuction() {
    if (whetherAuctionCouldBeCompleted()) {
      eventPublisher.publish(new AuctionCompleteEvent(getOngoingAuction()));
      return;
    }

    throw new AuctionCompletionException(
        "Auction could not be completed, because there is no ongoing auction at this moment."
    );
  }

  public boolean whetherAnyAuctionIsActive() {
    return getOngoingAuction() != null;
  }

  public boolean whetherAuctionCouldBeScheduled() {
    return auctionFacade.getAwaitingActionCount() < auctionsConfig.auctionQueueLimit;
  }

  public boolean whetherAuctionCouldBeCompleted() {
    return whetherAnyAuctionIsActive();
  }

  private boolean whetherAnyAuctionIsScheduled() {
    return auctionFacade.getAwaitingActionCount() > 0;
  }

  public Auction getOngoingAuction() {
    return ongoingAuction;
  }

  public void setOngoingAuction(final Auction ongoingAuction) {
    this.ongoingAuction = ongoingAuction;
  }
}
