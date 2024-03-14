package pl.auroramc.auctions.auction.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pl.auroramc.auctions.auction.Auction;

public class AuctionCompleteEvent extends BaseAuctionEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public AuctionCompleteEvent(final Auction auction) {
    super(auction);
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
