package pl.auroramc.gamble.stake;

import static pl.auroramc.commons.mutex.Mutex.mutex;

import java.util.ArrayList;
import java.util.List;
import pl.auroramc.commons.mutex.Mutex;

class StakeService implements StakeFacade {

  private final Mutex<List<StakeContext>> bunchOfStakeContexts;

  StakeService(final List<StakeContext> bunchOfStakeContexts) {
    this.bunchOfStakeContexts = mutex(bunchOfStakeContexts);
  }

  StakeService() {
    this(new ArrayList<>());
  }

  @Override
  public void createStakeContext(final StakeContext stakeContext) {
    bunchOfStakeContexts.mutate(
        bunch -> {
          bunch.add(stakeContext);
          return bunch;
        });
  }

  @Override
  public void deleteStakeContext(final StakeContext stakeContext) {
    bunchOfStakeContexts.mutate(
        bunch -> {
          bunch.remove(stakeContext);
          return bunch;
        });
  }

  @Override
  public List<StakeContext> getBunchOfStakeContexts() {
    return bunchOfStakeContexts.read();
  }
}
