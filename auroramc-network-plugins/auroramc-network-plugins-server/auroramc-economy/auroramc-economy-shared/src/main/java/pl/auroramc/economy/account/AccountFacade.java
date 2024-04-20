package pl.auroramc.economy.account;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface AccountFacade {

  CompletableFuture<Account> getAccount(final Long userId, final Long currencyId);

  CompletableFuture<Account> createAccount(final Account account);

  CompletableFuture<Void> updateAccount(final Account account);

  CompletableFuture<Void> deleteAccount(final Account account);

  CompletableFuture<Account> retrieveAccount(final Long userId, final Long currencyId);

  CompletableFuture<Void> transferOfBalance(
      final Account initiator,
      final Account receiver,
      final Long currencyId,
      final BigDecimal amount);
}
