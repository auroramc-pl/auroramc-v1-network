package pl.auroramc.gamble.stake;

import java.util.ArrayList;
import java.util.List;

class StakeService implements StakeFacade {

  private final List<StakeContext> bunchOfStakeContexts;

  StakeService(final List<StakeContext> bunchOfStakeContexts) {
    this.bunchOfStakeContexts = bunchOfStakeContexts;
  }

  StakeService() {
    this(new ArrayList<>());
  }

  @Override
  public void createStakeContext(final StakeContext stakeContext) {
    bunchOfStakeContexts.add(stakeContext);
  }

  @Override
  public void deleteStakeContext(final StakeContext stakeContext) {
    bunchOfStakeContexts.remove(stakeContext);
  }

  @Override
  public List<StakeContext> getBunchOfStakeContexts() {
    return bunchOfStakeContexts;
  }
}
