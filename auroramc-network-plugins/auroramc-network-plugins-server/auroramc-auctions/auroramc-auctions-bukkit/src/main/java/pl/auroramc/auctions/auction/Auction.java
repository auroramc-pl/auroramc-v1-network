package pl.auroramc.auctions.auction;

import static java.time.Instant.now;
import static pl.auroramc.commons.mutex.Mutex.mutex;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import pl.auroramc.commons.mutex.Mutex;

public class Auction {

  private final UUID auctionUniqueId;
  private final UUID vendorUniqueId;
  private final byte[] subject;
  private final Mutex<UUID> currentTraderUniqueId;
  private final Mutex<BigDecimal> currentOffer;
  private final Mutex<Instant> availableUntil;
  private BigDecimal minimalPrice;
  private BigDecimal minimalPricePuncture;
  private Instant availableSince;

  Auction(
      final UUID actionUniqueId,
      final UUID vendorUniqueId,
      final byte[] subject,
      final BigDecimal minimalPrice,
      final BigDecimal minimalPricePuncture,
      final Mutex<BigDecimal> currentOffer,
      final Mutex<UUID> currentTraderUniqueId
  ) {
    this.auctionUniqueId = actionUniqueId;
    this.vendorUniqueId = vendorUniqueId;
    this.subject = subject;
    this.minimalPrice = minimalPrice;
    this.minimalPricePuncture = minimalPricePuncture;
    this.currentOffer = currentOffer;
    this.currentTraderUniqueId = currentTraderUniqueId;
    this.availableUntil = mutex();
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

  public UUID getCurrentTraderUniqueId() {
    return currentTraderUniqueId.read();
  }

  public void setCurrentTraderUniqueId(final UUID currentTraderUniqueId) {
    this.currentTraderUniqueId.mutate(currentTraderUniqueId);
  }

  public BigDecimal getCurrentOffer() {
    return currentOffer.read();
  }

  public void setCurrentOffer(final BigDecimal currentOffer) {
    this.currentOffer.mutate(currentOffer);
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
    return availableUntil.read();
  }

  public void setAvailableUntil(final Instant availableUntil) {
    this.availableUntil.mutate(availableUntil);
  }

  public boolean whetherAuctionIsAvailable() {
    return (
        availableSince != null &&
            (
                availableUntil != null &&
                availableUntil.read().isAfter(now())
            )
    );
  }
}
