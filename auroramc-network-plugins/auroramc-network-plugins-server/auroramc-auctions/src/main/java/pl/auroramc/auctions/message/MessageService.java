package pl.auroramc.auctions.message;

import static com.spotify.futures.CompletableFutures.joinList;
import static java.time.Duration.ofSeconds;
import static org.bukkit.Bukkit.getOnlinePlayers;
import static pl.auroramc.commons.memoize.MemoizedSupplier.memoize;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import pl.auroramc.auctions.audience.Audience;
import pl.auroramc.auctions.audience.AudienceFacade;
import pl.auroramc.commons.tuplet.Pair;
import pl.auroramc.messages.message.MutableMessage;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;
import pl.auroramc.messages.message.compiler.CompiledMessage;

class MessageService implements MessageFacade {

  private final BukkitMessageCompiler messageCompiler;
  private final AudienceFacade audienceFacade;
  private final Supplier<CompletableFuture<List<Player>>> memoizedViewers;

  MessageService(final BukkitMessageCompiler messageCompiler, final AudienceFacade audienceFacade) {
    this.messageCompiler = messageCompiler;
    this.audienceFacade = audienceFacade;
    this.memoizedViewers = memoize(ofSeconds(30), () -> getViewers(getOnlinePlayers()));
  }

  @Override
  public void deliverMessage(final MutableMessage message) {
    final CompiledMessage compiledMessage = messageCompiler.compile(message);
    memoizedViewers
        .get()
        .thenApply(net.kyori.adventure.audience.Audience::audience)
        .thenAccept(compiledMessage::deliver);
  }

  private CompletableFuture<List<Player>> getViewers(final Collection<? extends Player> viewers) {
    return viewers.stream()
        .map(this::getViewerWithAudience)
        .collect(joinList())
        .thenApply(this::getViewers);
  }

  private List<Player> getViewers(final List<Pair<Player, Audience>> viewersByAudiences) {
    return viewersByAudiences.stream()
        .filter(viewerByAudience -> viewerByAudience.b().isAllowsMessages())
        .map(Pair::a)
        .toList();
  }

  private CompletableFuture<Pair<Player, Audience>> getViewerWithAudience(final Player viewer) {
    return audienceFacade
        .getAudienceByUniqueId(viewer.getUniqueId())
        .thenApply(audience -> new Pair<>(viewer, audience));
  }
}
