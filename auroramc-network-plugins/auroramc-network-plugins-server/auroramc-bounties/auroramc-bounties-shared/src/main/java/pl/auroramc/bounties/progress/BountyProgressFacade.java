package pl.auroramc.bounties.progress;

import java.util.concurrent.CompletableFuture;

public interface BountyProgressFacade {

  CompletableFuture<BountyProgress> retrieveBountyProgress(final Long userId);

  CompletableFuture<BountyProgress> getBountyProgressByUserId(final Long userId);

  CompletableFuture<BountyProgress> createBountyProgress(final BountyProgress bountyProgress);

  CompletableFuture<Void> updateBountyProgress(final BountyProgress bountyProgress);
}
