package pl.auroramc.auctions.auction;

import static java.time.Instant.now;
import static pl.auroramc.commons.concurrent.Mutex.mutex;
import static pl.auroramc.commons.lazy.Lazy.lazy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.commons.concurrent.Mutex;
import pl.auroramc.commons.lazy.Lazy;

public class Auction {

  private final UUID auctionUniqueId;
  private final UUID vendorUniqueId;
  private final byte[] subject;
  private final Mutex<UUID> currentTraderUniqueId;
  private final Mutex<BigDecimal> currentOffer;
  private final Mutex<Instant> availableUntil;
  private final Lazy<ItemStack> resolvedSubject;
  private BigDecimal minimalPrice;
  private BigDecimal minimalPricePuncture;
  private Instant availableSince;
  private int remainingTicks = 3;

  Auction(
      final UUID actionUniqueId,
      final UUID vendorUniqueId,
      final byte[] subject,
      final BigDecimal minimalPrice,
      final BigDecimal minimalPricePuncture,
      final Mutex<BigDecimal> currentOffer,
      final Mutex<UUID> currentTraderUniqueId) {
    this.auctionUniqueId = actionUniqueId;
    this.vendorUniqueId = vendorUniqueId;
    this.subject = subject;
    this.minimalPrice = minimalPrice;
    this.minimalPricePuncture = minimalPricePuncture;
    this.currentOffer = currentOffer;
    this.currentTraderUniqueId = currentTraderUniqueId;
    this.availableUntil = mutex();
    this.resolvedSubject = lazy(() -> ItemStack.deserializeBytes(subject));
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
    return (availableSince != null
        && (availableUntil != null && availableUntil.read().isAfter(now())));
  }

  public ItemStack getResolvedSubject() {
    return resolvedSubject.get();
  }

  public int getRemainingTicks() {
    return remainingTicks;
  }

  public int getAndDecrementRemainingTicks() {
    return remainingTicks--;
  }

  public void setRemainingTicks(final int remainingTicks) {
    this.remainingTicks = remainingTicks;
  }
}
