package pl.auroramc.bazaars.bazaar;

import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;
import org.bukkit.plugin.Plugin;
import pl.auroramc.bazaars.bazaar.transaction.context.BazaarTransactionContext;
import pl.auroramc.bazaars.message.MutableMessageSource;
import pl.auroramc.commons.message.MutableMessage;
import pl.auroramc.economy.EconomyFacade;
import pl.auroramc.economy.currency.Currency;

public interface BazaarFacade {

  static BazaarFacade getBazaarFacade(
      final Plugin plugin,
      final DecimalFormat priceFormat,
      final MutableMessageSource messageSource,
      final Currency fundsCurrency,
      final EconomyFacade economyFacade
  ) {
    return new BazaarService(
        plugin, priceFormat, messageSource, fundsCurrency, economyFacade
    );
  }

  CompletableFuture<MutableMessage> handleItemTransaction(
      final BazaarTransactionContext transactionContext
  );

  CompletableFuture<MutableMessage> handleItemPurchase(
      final BazaarTransactionContext transactionContext,
      final boolean whetherCustomerHasEnoughFunds
  );

  CompletableFuture<MutableMessage> handleItemSale(
      final BazaarTransactionContext transactionContext,
      final boolean whetherMerchantHasEnoughFunds
  );
}
