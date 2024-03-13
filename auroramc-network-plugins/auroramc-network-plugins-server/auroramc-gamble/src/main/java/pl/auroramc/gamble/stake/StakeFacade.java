package pl.auroramc.gamble.stake;

import java.util.List;

public interface StakeFacade {

  static StakeFacade getStakeFacade() {
    return new StakeService();
  }

  void createStakeContext(final StakeContext stakeContext);

  void deleteStakeContext(final StakeContext stakeContext);

  List<StakeContext> getBunchOfStakeContexts();
}
