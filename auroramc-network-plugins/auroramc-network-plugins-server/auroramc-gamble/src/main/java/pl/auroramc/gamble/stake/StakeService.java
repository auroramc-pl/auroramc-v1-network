package pl.auroramc.gamble.stake;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class StakeService implements StakeFacade {

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final List<StakeContext> bunchOfStakeContexts;

  StakeService(final List<StakeContext> bunchOfStakeContexts) {
    this.bunchOfStakeContexts = bunchOfStakeContexts;
  }

  StakeService() {
    this(new ArrayList<>());
  }

  @Override
  public void createStakeContext(final StakeContext stakeContext) {
    lock.writeLock().lock();
    try {
      bunchOfStakeContexts.add(stakeContext);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void deleteStakeContext(final StakeContext stakeContext) {
    lock.writeLock().lock();
    try {
      bunchOfStakeContexts.remove(stakeContext);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public List<StakeContext> getBunchOfStakeContexts() {
    lock.readLock().lock();
    try {
      return List.copyOf(bunchOfStakeContexts);
    } finally {
      lock.readLock().unlock();
    }
  }
}
