package pl.auroramc.bounties.progress;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDate.EPOCH;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static pl.auroramc.commons.scheduler.SchedulerPoll.ASYNC;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.commons.concurrent.CompletableFutureUtils;
import pl.auroramc.commons.scheduler.Scheduler;

class BountyProgressService implements BountyProgressFacade {

  private static final long INITIAL_DAY = 0;
  private final Scheduler scheduler;
  private final BountyProgressRepository bountyProgressRepository;
  private final AsyncLoadingCache<Long, BountyProgress> bountyProgressByUserId;

  BountyProgressService(
      final Scheduler scheduler, final BountyProgressRepository bountyProgressRepository) {
    this.scheduler = scheduler;
    this.bountyProgressRepository = bountyProgressRepository;
    this.bountyProgressByUserId =
        Caffeine.newBuilder()
            .expireAfterWrite(ofSeconds(30))
            .buildAsync(bountyProgressRepository::findBountyProgressByUserId);
  }

  @Override
  public CompletableFuture<BountyProgress> retrieveBountyProgress(final Long userId) {
    return getBountyProgressByUserId(userId)
        .thenCompose(bountyProgress -> createBountyProgressIfNotExists(userId, bountyProgress));
  }

  @Override
  public CompletableFuture<BountyProgress> getBountyProgressByUserId(final Long userId) {
    return bountyProgressByUserId
        .get(userId)
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  @Override
  public CompletableFuture<BountyProgress> createBountyProgress(
      final BountyProgress bountyProgress) {
    return scheduler
        .run(ASYNC, () -> bountyProgressRepository.createBountyProgress(bountyProgress))
        .thenAccept(
            state ->
                bountyProgressByUserId.put(
                    bountyProgress.getUserId(), completedFuture(bountyProgress)))
        .exceptionally(CompletableFutureUtils::delegateCaughtException)
        .thenCompose(state -> getBountyProgressByUserId(bountyProgress.getUserId()));
  }

  @Override
  public CompletableFuture<Void> updateBountyProgress(final BountyProgress bountyProgress) {
    return scheduler
        .run(ASYNC, () -> bountyProgressRepository.updateBountyProgress(bountyProgress))
        .exceptionally(CompletableFutureUtils::delegateCaughtException);
  }

  private CompletableFuture<BountyProgress> createBountyProgressIfNotExists(
      final Long userId, BountyProgress bountyProgress) {
    if (bountyProgress == null) {
      bountyProgress =
          BountyProgressBuilder.newBuilder()
              .withUserId(userId)
              .withDay(INITIAL_DAY)
              .withAcquisitionDate(EPOCH)
              .build();
      return createBountyProgress(bountyProgress);
    }

    return completedFuture(bountyProgress);
  }
}
