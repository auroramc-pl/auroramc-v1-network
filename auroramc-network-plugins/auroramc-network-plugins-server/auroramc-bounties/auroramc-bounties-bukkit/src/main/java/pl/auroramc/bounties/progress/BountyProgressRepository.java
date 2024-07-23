package pl.auroramc.bounties.progress;

interface BountyProgressRepository {

  BountyProgress findBountyProgressByUserId(final Long userId);

  void createBountyProgress(final BountyProgress bountyProgress);

  void updateBountyProgress(final BountyProgress bountyProgress);
}
