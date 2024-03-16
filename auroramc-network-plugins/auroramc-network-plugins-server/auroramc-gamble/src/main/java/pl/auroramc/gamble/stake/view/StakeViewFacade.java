package pl.auroramc.gamble.stake.view;

import java.util.Collection;
import java.util.Optional;
import org.bukkit.inventory.Inventory;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MessageSource;
import pl.auroramc.gamble.stake.StakeFacade;

public interface StakeViewFacade {

  static StakeViewFacade getStakeViewFacade(
      final StakeFacade stakeFacade,
      final Currency fundsCurrency,
      final MessageSource messageSource
  ) {
    return new StakeViewService(
        stakeFacade, fundsCurrency, messageSource
    );
  }

  void recalculate();

  Optional<Inventory> getStakeView(final int pageIndex);

  Collection<Inventory> getStakeViews();

  int getPageCount();
}
