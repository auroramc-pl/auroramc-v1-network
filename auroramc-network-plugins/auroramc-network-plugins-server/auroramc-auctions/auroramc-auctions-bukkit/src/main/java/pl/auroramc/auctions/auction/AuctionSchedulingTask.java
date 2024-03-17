package pl.auroramc.auctions.auction;

public class AuctionSchedulingTask implements Runnable {

  private final AuctionFacade auctionFacade;
  private final AuctionController auctionController;

  public AuctionSchedulingTask(
      final AuctionFacade auctionFacade,
      final AuctionController auctionController
  ) {
    this.auctionFacade = auctionFacade;
    this.auctionController = auctionController;
  }

  @Override
  public void run() {
    final Auction activeAuction = auctionFacade.getActiveAuction();
    if (activeAuction != null) {
      return;
    }

    final Auction pooledAuction = auctionFacade.getPooledAuction();
    if (pooledAuction == null) {
      return;
    }

    auctionController.scheduleAuction(pooledAuction);
  }
}
