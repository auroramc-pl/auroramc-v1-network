package pl.auroramc.bazaars.bazaar;

import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

public interface BazaarFacade {

  static BazaarFacade getBazaarFacade(
      final Plugin plugin,
      final DecimalFormat priceFormat,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade
  ) {
    return new BazaarService(plugin, priceFormat, fundsCurrency, economyFacade);
  }

  CompletableFuture<Component> handleItemTransaction(
      final BazaarTransactionContext transactionContext
  );

  CompletableFuture<Component> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds
  );

  CompletableFuture<Component> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds
  );
}
