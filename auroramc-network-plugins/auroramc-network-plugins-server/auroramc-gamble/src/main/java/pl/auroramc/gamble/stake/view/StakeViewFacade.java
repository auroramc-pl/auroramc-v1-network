package pl.auroramc.gamble.stake.view;

import java.util.Collection;
import java.util.Optional;
import org.bukkit.inventory.Inventory;
import pl.auroramc.commons.scheduler.Scheduler;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.StakeFacade;
import pl.auroramc.messages.message.compiler.BukkitMessageCompiler;

public interface StakeViewFacade {

  static StakeViewFacade getStakeViewFacade(
      final Scheduler scheduler,
      final StakeFacade stakeFacade,
      final Currency fundsCurrency,
      final MessageSource messageSource,
      final BukkitMessageCompiler messageCompiler) {
    return new StakeViewService(
        scheduler, stakeFacade, fundsCurrency, messageSource, messageCompiler);
  }

  void recalculate();

  Optional<Inventory> getStakeView(final int pageIndex);

  Collection<Inventory> getStakeViews();

  int getPageCount();
}
