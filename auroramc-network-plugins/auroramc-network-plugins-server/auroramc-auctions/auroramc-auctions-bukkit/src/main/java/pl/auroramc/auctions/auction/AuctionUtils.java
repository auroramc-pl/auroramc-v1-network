package pl.auroramc.auctions.auction;

import static java.math.BigDecimal.ZERO;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;
import static pl.auroramc.commons.collection.CollectionUtils.merge;
import static pl.auroramc.commons.decimal.DecimalFormatter.getFormattedDecimal;
import static pl.auroramc.commons.item.ItemStackFormatter.getFormattedItemStack;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.auroramc.auctions.auction.event.AuctionBidEvent;
import pl.auroramc.auctions.auction.event.AuctionCompleteEvent;
import pl.auroramc.auctions.auction.event.AuctionScheduleEvent;
import pl.auroramc.auctions.auction.event.BaseAuctionEvent;
import pl.auroramc.economy.currency.Currency;

final class AuctionUtils {

  private static final Component UNKNOWN_DISPLAY_NAME = miniMessage().deserialize("<gray>Nieznany");
  private static final Component UNKNOWN_AUCTION_BID = miniMessage().deserialize("<white>Brak");

  private AuctionUtils() {

  }

  static List<TagResolver> getAuctionDefaultResolvers(
      final BaseAuctionEvent event,
      final Component vendorName,
      final Component alteredAuctionName
  ) {
    final ItemStack parsedSubject = event.getSubject();
    return List.of(
        component("vendor", vendorName),
        component("subject", getFormattedItemStack(parsedSubject)),
        component("auction_uuid",
            alteredAuctionName
                .hoverEvent(getAuctionUuidWithHoverDisplay(event.getAuction()))
        )
    );
  }

  static TagResolver[] getAuctionSchedulingResolvers(
      final AuctionScheduleEvent event, final Component vendorName) {
    final List<TagResolver> auctionSchedulingResolvers = List.of(
        unparsed("min_offer",
            getFormattedDecimal(event.getAuction().getMinimalPrice())
        ),
        unparsed("min_offer_bid",
            getFormattedDecimal(event.getAuction().getMinimalPricePuncture())
        )
    );

    return merge(
        getAuctionDefaultResolvers(event, vendorName, text("aukcję")),
        auctionSchedulingResolvers,
        TagResolver[]::new
    );
  }

  static TagResolver[] getAuctionCompletingResolvers(
      final AuctionCompleteEvent event, final Component vendorName
  ) {
    return getAuctionDefaultResolvers(event, vendorName, text("Aukcja"))
        .toArray(TagResolver[]::new);
  }

  static TagResolver[] getAuctionBiddingResolvers(
      final AuctionBidEvent event,
      final Component vendorName,
      final Component traderName
  ) {
    final List<TagResolver> auctionBiddingResolvers = List.of(
        component("current_trader", traderName),
        unparsed("current_offer",
            getFormattedDecimal(
                Optional.ofNullable(event.getAuction().getCurrentOffer())
                    .orElse(ZERO)
            )
        )
    );

    return merge(
        getAuctionDefaultResolvers(event, vendorName, text("aukcję")),
        auctionBiddingResolvers,
        TagResolver[]::new
    );
  }

  static TagResolver[] getAuctionCompletingResolversWithTrader(
      final AuctionCompleteEvent event,
      final Component vendorName,
      final Component traderName
  ) {
    final List<TagResolver> auctionSchedulingResolvers = List.of(
        component("current_trader", traderName),
        unparsed("current_offer",
            getFormattedDecimal(
                Optional.ofNullable(event.getAuction().getCurrentOffer())
                    .orElse(ZERO)
            )
        )
    );

    return merge(
        getAuctionDefaultResolvers(event, vendorName, text("Aukcja")),
        auctionSchedulingResolvers,
        TagResolver[]::new
    );
  }

  static TagResolver[] getAuctionNotifyingResolvers(
      final Auction auction,
      final Component alteredAuctionName,
      final Duration remainingDurationOfAuction
  ) {
    final ItemStack parsedSubject = ItemStack.deserializeBytes(auction.getSubject());
    return List.of(
        component("subject", getFormattedItemStack(parsedSubject)),
        component("auction_uuid",
            alteredAuctionName
                .hoverEvent(getAuctionUuidWithHoverDisplay(auction))
        ),
        unparsed("period", "%ss".formatted(remainingDurationOfAuction.getSeconds()))
    ).toArray(TagResolver[]::new);
  }

  static TagResolver[] getAuctionSummaryResolvers(
      final Auction auction, final Currency fundsCurrency) {
    return List.of(
            component("auction_uuid",
                text("aukcji")
                    .hoverEvent(getAuctionUuidWithHoverDisplay(auction))),
            component("subject",
                getFormattedItemStack(ItemStack.deserializeBytes(auction.getSubject()))
            ),
            component("vendor",
                getDisplayNameOfPlayerOrDefault(auction.getVendorUniqueId())
            ),
            component("highest_bid", getWinningBidSummary(auction, fundsCurrency)),
            unparsed("funds_currency", fundsCurrency.getSymbol()),
            unparsed("minimal_price",
                getFormattedDecimal(auction.getMinimalPrice())
            ),
            unparsed("minimal_price_puncture",
                getFormattedDecimal(auction.getMinimalPricePuncture())
            )
        )
        .toArray(TagResolver[]::new);
  }

  private static Component getWinningBidSummary(final Auction auction, final Currency fundsCurrency) {
    if (auction.getTraderUniqueId() == null) {
      return UNKNOWN_AUCTION_BID;
    }

    return miniMessage().deserialize(
        "<white><funds_currency><offer> <dark_gray>(<white><trader><dark_gray>)",
        unparsed("funds_currency", fundsCurrency.getSymbol()),
        unparsed("offer",
            getFormattedDecimal(
                Optional.ofNullable(auction.getCurrentOffer())
                    .orElse(ZERO)
            )
        ),
        component("trader",
            getDisplayNameOfPlayerOrDefault(auction.getTraderUniqueId())
        )
    );
  }

  private static Component getDisplayNameOfPlayerOrDefault(final UUID playerUniqueId) {
    return Optional.ofNullable(playerUniqueId)
        .map(Bukkit::getPlayer)
        .map(Player::name)
        .orElse(UNKNOWN_DISPLAY_NAME);
  }

  static Component getAuctionUuidWithHoverDisplay(final Auction auction) {
    return empty()
        .append(text("Unikalny identyfikator: ").color(NamedTextColor.GRAY))
        .append(text(auction.getAuctionUniqueId().toString()));
  }
}
