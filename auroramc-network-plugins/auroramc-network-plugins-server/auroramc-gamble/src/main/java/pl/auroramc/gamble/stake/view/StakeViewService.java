package pl.auroramc.gamble.stake.view;

import static java.util.List.copyOf;
import static java.util.stream.Collectors.toMap;
import static pl.auroramc.commons.collection.CollectionUtils.partition;
import static pl.auroramc.commons.scheduler.SchedulerPoll.SYNC;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.context.StakeContext;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

class StakeViewService implements StakeViewFacade {

  private static final int INITIAL_STAKE_PAGE_INDEX = 0;
  private static final int STAKES_PER_PAGE = 4 * 7;
  private final StampedLock lock = new StampedLock();
  private final Map<Integer, StakeView> stakeViewByPageIndex;
  private final Scheduler scheduler;
  private final StakeFacade stakeFacade;
  private final Currency fundsCurrency;
  private final MessageSource messageSource;
  private final BukkitMessageCompiler messageCompiler;

  StakeViewService(
      final Scheduler scheduler,
      final StakeFacade stakeFacade,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    this.scheduler = scheduler;
    this.stakeViewByPageIndex = new ConcurrentHashMap<>();
    this.stakeFacade = stakeFacade;
    this.fundsCurrency = fundsCurrency;
    this.messageSource = messageSource;
    this.messageCompiler = messageCompiler;
  }

  @Override
  public void recalculate() {
    final Map<Integer, List<HumanEntity>> pageIndexesToViewers =
        stakeViewByPageIndex.values().stream()
            .collect(
                toMap(
                    StakeView::getPageIndex,
                    stakeView -> copyOf(stakeView.getInventory().getViewers())));

    final long stamp = lock.writeLock();
    try {
      recalculateViews();
    } finally {
      lock.unlockWrite(stamp);
    }

    updateViewsForViewers(pageIndexesToViewers);
  }

  private void recalculateViews() {
    stakeViewByPageIndex.clear();

    final List<List<StakeContext>> partitions =
        partition(stakeFacade.getBunchOfStakeContexts(), STAKES_PER_PAGE);
    for (final List<StakeContext> partition : partitions) {
      final int pageIndex = partitions.indexOf(partition);
      stakeViewByPageIndex.put(
          pageIndex,
          new StakeView(pageIndex, fundsCurrency, messageSource, messageCompiler, partition));
    }
  }

  private void updateViewsForViewers(final Map<Integer, List<HumanEntity>> pageIndexesToViewers) {
    for (final Entry<Integer, List<HumanEntity>> pageIndexToViewers :
        pageIndexesToViewers.entrySet()) {
      final List<HumanEntity> viewers = pageIndexToViewers.getValue();
      getStakeView(pageIndexToViewers.getKey())
          .or(() -> getStakeView(INITIAL_STAKE_PAGE_INDEX))
          .ifPresentOrElse(
              inventory ->
                  scheduler.run(
                      SYNC, () -> viewers.forEach(viewer -> viewer.openInventory(inventory))),
              () -> scheduler.run(SYNC, () -> viewers.forEach(HumanEntity::closeInventory)));
    }
  }

  @Override
  public Optional<Inventory> getStakeView(final int pageIndex) {
    return Optional.ofNullable(stakeViewByPageIndex.get(pageIndex))
        .map(InventoryHolder::getInventory);
  }

  @Override
  public Collection<Inventory> getStakeViews() {
    return stakeViewByPageIndex.values().stream().map(InventoryHolder::getInventory).toList();
  }

  @Override
  public int getPageCount() {
    return stakeViewByPageIndex.size();
  }
}
