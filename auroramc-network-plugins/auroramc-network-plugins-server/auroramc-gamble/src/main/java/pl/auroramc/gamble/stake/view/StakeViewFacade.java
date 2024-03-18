package pl.auroramc.gamble.stake.view;

import java.util.Collection;
import java.util.Optional;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import pl.auroramc.economy.currency.Currency;
import pl.auroramc.gamble.message.MutableMessageSource;
import pl.auroramc.gamble.stake.StakeFacade;

public interface StakeViewFacade {

  static StakeViewFacade getStakeViewFacade(
      final Plugin plugin,
      final StakeFacade stakeFacade,
      final Currency fundsCurrency,
      final MutableMessageSource messageSource
  ) {
    return new StakeViewService(
        plugin, stakeFacade, fundsCurrency, messageSource
    );
  }

  void recalculate();

  Optional<Inventory> getStakeView(final int pageIndex);

  Collection<Inventory> getStakeViews();

  int getPageCount();
}
