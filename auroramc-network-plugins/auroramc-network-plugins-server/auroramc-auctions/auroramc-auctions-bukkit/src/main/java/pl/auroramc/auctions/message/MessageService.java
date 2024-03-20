package pl.auroramc.auctions.message;

import static com.spotify.futures.CompletableFutures.joinList;
import static java.time.Duration.ofSeconds;
import static org.bukkit.Bukkit.getOnlinePlayers;
import static pl.auroramc.commons.Tuple.tupleOf;
import static pl.auroramc.commons.memoize.MemoizedSupplier.memoize;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.commons.Tuple;
import pl.auroramc.commons.message.delivery.DeliverableMutableMessage;

class MessageService implements MessageFacade {


  private final AudienceFacade audienceFacade;
  private final Supplier<CompletableFuture<List<Player>>> memoizedViewers;

  MessageService(final AudienceFacade audienceFacade) {
    this.audienceFacade = audienceFacade;
    this.memoizedViewers = memoize(ofSeconds(30), () -> getViewers(getOnlinePlayers()));
  }

  @Override
  public void deliverMessage(final DeliverableMutableMessage message) {
    memoizedViewers.get()
        .thenApply(net.kyori.adventure.audience.Audience::audience)
        .thenAccept(message::deliver);
  }

  private CompletableFuture<List<Player>> getViewers(
      final Collection<? extends Player> viewers
  ) {
    return viewers.stream()
        .map(this::getViewerWithAudience)
        .collect(joinList())
        .thenApply(this::getViewers);
  }

  private List<Player> getViewers(
      final List<Tuple<Player, Audience>> viewersByAudiences
  ) {
    return viewersByAudiences.stream()
        .filter(viewerByAudience -> viewerByAudience.getB().isAllowsMessages())
        .map(Tuple::getA)
        .toList();
  }

  private CompletableFuture<Tuple<Player, Audience>> getViewerWithAudience(
      final Player viewer
  ) {
    return audienceFacade.getAudienceByUniqueId(viewer.getUniqueId())
        .thenApply(audience -> tupleOf(viewer, audience));
  }
}
