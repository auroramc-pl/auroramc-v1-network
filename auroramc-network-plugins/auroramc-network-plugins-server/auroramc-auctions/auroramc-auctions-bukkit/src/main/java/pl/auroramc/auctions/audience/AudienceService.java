package pl.auroramc.auctions.audience;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.CompletableFuture.runAsync;
import static pl.auroramc.commons.ExceptionUtils.delegateCaughtException;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

class AudienceService implements AudienceFacade {

  private final Logger logger;
  private final AudienceRepository audienceRepository;
  private final AsyncLoadingCache<UUID, Audience> audienceByUniqueId;

  AudienceService(final Logger logger, final AudienceRepository audienceRepository) {
    this.logger = logger;
    this.audienceRepository = audienceRepository;
    this.audienceByUniqueId =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(20))
            .buildAsync(audienceRepository::findAudienceByUniqueId);
  }

  @Override
  public CompletableFuture<Audience> getAudienceByUniqueId(final UUID uniqueId) {
    return audienceByUniqueId.get(uniqueId);
  }

  @Override
  public CompletableFuture<Void> createAudience(final Audience audience) {
    return runAsync(() -> audienceRepository.createAudience(audience))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }

  @Override
  public CompletableFuture<Void> updateAudience(final Audience audience) {
    return runAsync(() -> audienceRepository.updateAudience(audience))
        .exceptionally(exception -> delegateCaughtException(logger, exception));
  }
}
