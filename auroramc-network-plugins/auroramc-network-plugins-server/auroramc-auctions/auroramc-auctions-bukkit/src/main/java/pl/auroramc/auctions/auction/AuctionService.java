package pl.auroramc.auctions.auction;

import java.util.LinkedList;
import java.util.Queue;

class AuctionService implements AuctionFacade {

  private static final Object SCHEDULE_LOCK = new Object();
  private static final Object COUNTING_LOCK = new Object();
  private final Queue<Auction> auctionQueue;

  AuctionService() {
    this.auctionQueue = new LinkedList<>();
  }

  @Override
  public Auction getNextAuction() {
    synchronized (SCHEDULE_LOCK) {
      return auctionQueue.poll();
    }
  }

  @Override
  public void queueAuction(final Auction auction) {
    synchronized (SCHEDULE_LOCK) {
      auctionQueue.add(auction);
    }
  }

  @Override
  public int getAwaitingActionCount() {
    synchronized (COUNTING_LOCK) {
      return auctionQueue.size();
    }
  }
}
