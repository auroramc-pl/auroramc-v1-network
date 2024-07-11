package pl.auroramc.auctions.auction;

import static java.math.RoundingMode.HALF_DOWN;

import java.math.BigDecimal;

final class AuctionUtils {

  private AuctionUtils() {}

  static BigDecimal getPotentialOffer(final Auction auction, final BigDecimal offer) {
    if (offer != null) {
      return offer.setScale(2, HALF_DOWN);
    }

    if (auction.getCurrentOffer() == null) {
      return auction.getMinimalPrice().setScale(2, HALF_DOWN);
    }

    return auction.getCurrentOffer().add(auction.getMinimalPricePuncture()).setScale(2, HALF_DOWN);
  }
}
