package pl.auroramc.auctions.auction.event;

import static pl.auroramc.commons.lazy.Lazy.lazy;

import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.auctions.auction.Auction;
import pl.auroramc.commons.lazy.Lazy;

public abstract class BaseAuctionEvent extends Event {

  private final Auction auction;
  private final Lazy<ItemStack> subject;

  protected BaseAuctionEvent(final Auction auction) {
    this.auction = auction;
    this.subject = lazy(() -> ItemStack.deserializeBytes(auction.getSubject()));
  }

  public Optional<Player> getVendor() {
    return Optional.ofNullable(auction.getVendorUniqueId())
        .map(Bukkit::getPlayer);
  }

  public Optional<Player> getTrader() {
    return Optional.ofNullable(auction.getTraderUniqueId())
        .map(Bukkit::getPlayer);
  }

  public Auction getAuction() {
    return auction;
  }

  public ItemStack getSubject() {
    return subject.get();
  }
}
