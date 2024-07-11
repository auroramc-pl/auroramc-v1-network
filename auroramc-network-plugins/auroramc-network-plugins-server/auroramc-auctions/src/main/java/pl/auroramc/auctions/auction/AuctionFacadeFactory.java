package pl.auroramc.auctions.auction;

public final class AuctionFacadeFactory {

  private AuctionFacadeFactory() {}

  public static AuctionFacade getAuctionFacade() {
    return new AuctionService();
  }
}
