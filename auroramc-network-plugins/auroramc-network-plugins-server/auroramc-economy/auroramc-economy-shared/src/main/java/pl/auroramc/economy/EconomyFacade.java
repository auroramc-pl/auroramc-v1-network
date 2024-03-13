package pl.auroramc.economy;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import pl.auroramc.economy.currency.Currency;

public interface EconomyFacade {

  CompletableFuture<BigDecimal> balance(
      final UUID uniqueId, final Currency currency
  );

  CompletableFuture<Void> balance(
      final UUID uniqueId, final Currency currency, final BigDecimal amount
  );

  CompletableFuture<Void> deposit(
      final UUID uniqueId, final Currency currency, final BigDecimal amount
  );

  CompletableFuture<Void> withdraw(
      final UUID uniqueId, final Currency currency, final BigDecimal amount
  );

  CompletableFuture<Void> transfer(
      final UUID initiatorUniqueId,
      final UUID receiverUniqueId,
      final Currency currency,
      final BigDecimal amount
  );

  CompletableFuture<Boolean> has(
      final UUID uniqueId, final Currency currency, final BigDecimal amount
  );
}
