package pl.auroramc.auctions.auction;

public interface AuctionFacade {

  void addAuction(final Auction auction);

  int getAuctionCount();

  Auction getPooledAuction();

  Auction getActiveAuction();

  void setActiveAuction(final Auction auction);
}
