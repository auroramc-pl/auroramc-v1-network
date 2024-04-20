package pl.auroramc.auctions.audience;

import static java.time.Duration.ofSeconds;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.commons.scheduler.caffeine.CaffeineExecutor;

class AudienceService implements AudienceFacade {

  private final Scheduler scheduler;
  private final AudienceRepository audienceRepository;
  private final AsyncLoadingCache<UUID, Audience> audienceByUniqueId;

  AudienceService(final Scheduler scheduler, final AudienceRepository audienceRepository) {
    this.scheduler = scheduler;
    this.audienceRepository = audienceRepository;
    this.audienceByUniqueId =
        Caffeine.newBuilder()
            .executor(new CaffeineExecutor(scheduler))
            .expireAfterWrite(ofSeconds(20))
            .buildAsync(audienceRepository::findAudienceByUniqueId);
  }

  @Override
  public CompletableFuture<Audience> getAudienceByUniqueId(final UUID uniqueId) {
    return audienceByUniqueId.get(uniqueId);
  }

  @Override
  public CompletableFuture<Void> createAudience(final Audience audience) {
    return scheduler
        .run(ASYNC, () -> audienceRepository.createAudience(audience))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<Void> updateAudience(final Audience audience) {
    return scheduler
        .run(ASYNC, () -> audienceRepository.updateAudience(audience))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }
}
