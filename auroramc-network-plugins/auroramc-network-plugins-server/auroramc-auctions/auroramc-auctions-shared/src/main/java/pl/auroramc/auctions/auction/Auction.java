package pl.auroramc.auctions.auction;

import static java.time.Instant.now;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Auction {

  private final UUID auctionUniqueId;
  private final UUID vendorUniqueId;
  private UUID traderUniqueId;
  private final byte[] subject;
  private BigDecimal currentOffer;
  private BigDecimal minimalPrice;
  private BigDecimal minimalPricePuncture;
  private Instant availableSince;
  private Instant availableUntil;

  Auction(
      final UUID actionUniqueId,
      final UUID vendorUniqueId,
      final UUID traderUniqueId,
      final byte[] subject,
      final BigDecimal currentOffer,
      final BigDecimal minimalPrice,
      final BigDecimal minimalPricePuncture
  ) {
    this.auctionUniqueId = actionUniqueId;
    this.vendorUniqueId = vendorUniqueId;
    this.traderUniqueId = traderUniqueId;
    this.subject = subject;
    this.currentOffer = currentOffer;
    this.minimalPrice = minimalPrice;
    this.minimalPricePuncture = minimalPricePuncture;
  }

  Auction(
      final UUID vendorUniqueId,
      final byte[] subject,
      final BigDecimal minimalPrice,
      final BigDecimal minimalPricePuncture
  ) {
    this(
        UUID.randomUUID(),
        vendorUniqueId,
        null,
        subject,
        null,
        minimalPrice,
        minimalPricePuncture
    );
  }

  public UUID getAuctionUniqueId() {
    return auctionUniqueId;
  }

  public byte[] getSubject() {
    return subject;
  }

  public UUID getVendorUniqueId() {
    return vendorUniqueId;
  }

  public UUID getTraderUniqueId() {
    return traderUniqueId;
  }

  public void setTraderUniqueId(final UUID traderUniqueId) {
    this.traderUniqueId = traderUniqueId;
  }

  public BigDecimal getCurrentOffer() {
    return currentOffer;
  }

  public void setCurrentOffer(final BigDecimal currentOffer) {
    this.currentOffer = currentOffer;
  }

  public BigDecimal getMinimalPrice() {
    return minimalPrice;
  }

  public void setMinimalPrice(final BigDecimal minimalPrice) {
    this.minimalPrice = minimalPrice;
  }

  public BigDecimal getMinimalPricePuncture() {
    return minimalPricePuncture;
  }

  public void setMinimalPricePuncture(final BigDecimal minimalPricePuncture) {
    this.minimalPricePuncture = minimalPricePuncture;
  }

  public Instant getAvailableSince() {
    return availableSince;
  }

  public void setAvailableSince(final Instant availableSince) {
    this.availableSince = availableSince;
  }

  public Instant getAvailableUntil() {
    return availableUntil;
  }

  public void setAvailableUntil(final Instant availableUntil) {
    this.availableUntil = availableUntil;
  }

  public boolean whetherAuctionIsAvailable() {
    return (
        availableSince != null &&
            (
                availableUntil != null &&
                availableUntil.isAfter(now())
            )
    );
  }
}
