package pl.auroramc.auctions.auction;

class AuctionCompletionException extends IllegalStateException {

  AuctionCompletionException(final String message) {
    super(message);
  }
}
