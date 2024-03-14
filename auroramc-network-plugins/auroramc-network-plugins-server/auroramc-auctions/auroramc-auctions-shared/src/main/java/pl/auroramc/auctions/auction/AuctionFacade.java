package pl.auroramc.auctions.auction;

public interface AuctionFacade {

  Auction getNextAuction();

  void queueAuction(final Auction auction);

  int getAwaitingActionCount();
}
