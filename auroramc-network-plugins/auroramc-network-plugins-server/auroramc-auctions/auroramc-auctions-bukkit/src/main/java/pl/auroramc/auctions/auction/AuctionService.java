package pl.auroramc.auctions.auction;

import static pl.auroramc.commons.mutex.Mutex.mutex;

import java.util.LinkedList;
import java.util.Queue;
import pl.auroramc.commons.mutex.Mutex;

class AuctionService implements AuctionFacade {

  private final Mutex<Queue<Auction>> auctionQueue;
  private final Mutex<Auction> activeAuction;

  AuctionService() {
    this.auctionQueue = mutex(new LinkedList<>());
    this.activeAuction = mutex();
  }

  @Override
  public void addAuction(final Auction auction) {
    auctionQueue.mutate(
        queue -> {
          queue.add(auction);
          return queue;
        });
  }

  @Override
  public int getAuctionCount() {
    return auctionQueue.read(Queue::size);
  }

  @Override
  public Auction getPooledAuction() {
    return auctionQueue.read(Queue::poll);
  }

  @Override
  public Auction getActiveAuction() {
    return activeAuction.read();
  }

  @Override
  public void setActiveAuction(final Auction activeAuction) {
    this.activeAuction.mutate(activeAuction);
  }
}
